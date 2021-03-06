package com.blockscore.net;

import com.blockscore.common.Constants;
import com.blockscore.models.Candidate;
import com.blockscore.models.Company;
import com.blockscore.models.PaginatedResult;
import com.blockscore.models.Person;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.DatatypeConverter;

/**
 * The Blockscore Java API client.
 */
public class BlockscoreApiClient {
  private static RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.NONE;
  private String apiKey;

  private final BlockscoreRestAdapter restAdapter;

  /**
   * Turns on/off logging. Must be set before creating API client to take effect.
   *
   * @param useVerboseLogs  whether or not to use verbose network logs.
   */
  public static void useVerboseLogs(final boolean useVerboseLogs) {
    if (useVerboseLogs) {
      logLevel = RestAdapter.LogLevel.FULL;
    } else {
      logLevel = RestAdapter.LogLevel.NONE;
    }
  }

  /**
   * Creates a BlockscoreApiClient. Requires a valid API key for construction to occur successfully.
   *
   * @param apiKey  the valid Blockscore API key
   */
  public BlockscoreApiClient(@NotNull final String apiKey) {
    this.apiKey = apiKey + ":";

    RestAdapter.Builder restBuilder = new RestAdapter.Builder().setClient(new BlockscoreHttpClient())
                                                               .setEndpoint(Constants.BLOCKSCORE_DOMAIN);
    restBuilder.setConverter(getDefaultConverter());
    restBuilder.setRequestInterceptor(getDefaultRequestInterceptor());
    restBuilder.setErrorHandler(new BlockscoreErrorHandler());
    restBuilder.setLogLevel(logLevel);

    restAdapter = restBuilder.build().create(BlockscoreRestAdapter.class);
  }

  /**
   * Gets a single person exactly as it was when you created it.
   * This route is useful for auditing purposes as you can provide proof that a verification took place
   * along with all of its associated data.
   *
   * @param id  ID of Person.
   * @return the person, not null
   */
  @NotNull
  public Person retrievePerson(@NotNull final String id) {
    Person person = restAdapter.retrievePerson(id);
    person.setAdapter(restAdapter);
    return person;
  }

  /**
   * Lists a historical record of all verifications that you have completed.
   * The list is displayed in reverse chronological order (newer people appear first).
   *
   * @return the historical listing of created people, not null
   */
  @NotNull
  public PaginatedResult<Person> listPeople() {
    PaginatedResult<Person> result = restAdapter.listPeople();

    for (Person person : result.getData()) {
      person.setAdapter(restAdapter);
    }

    return result;
  }

  /**
   * Gets a single company exactly as it was when you created it.
   * This route is useful for auditing purposes as you can provide proof that a company verification took place
   * along with all of its associated data.
   *
   * @param id  ID of the Company.
   * @return the company, not null
   */
  @NotNull
  public Company retrieveCompany(@NotNull final String id) {
    return restAdapter.retrieveCompany(id);
  }

  /**
   * Lists verified companies a historical record of all company verifications that you have completed.
   * The list is displayed in reverse chronological order (newer company verifications appear first).
   *
   * @return the historical listing of created companies, not null
   */
  @NotNull
  public PaginatedResult<Company> listCompanies() {
    return restAdapter.listCompanies();
  }

  /**
   * Retrieves a candidate.
   *
   * @param id  ID of the candidate.
   * @return the candidate, not null
   */
  @NotNull
  public Candidate retrieveCandidate(@NotNull final String id) {
    Candidate candidate = restAdapter.retrieveCandidate(id);
    candidate.setAdapter(restAdapter);
    return candidate;
  }

  /**
   * Lists a historical record of all candidates you have created.
   * The list is displayed in reverse chronological order (newer candidates appear first).
   *
   * @return the historical listing of created candidates, not null
   */
  @NotNull
  public PaginatedResult<Candidate> listCandidates() {
    PaginatedResult<Candidate> result = restAdapter.listCandidates();

    for (Candidate candidate : result.getData()) {
      candidate.setAdapter(restAdapter);
    }

    return result;
  }

  /**
   * Encodes the API key for Basic authentication.
   *
   * @return the API key with Base 64 encoding
   */
  @NotNull
  private String getEncodedAuthorization() {
    try {
      return "Basic " + DatatypeConverter.printBase64Binary(apiKey.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  private JacksonConverter getDefaultConverter() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibilityChecker(mapper.getSerializationConfig()
                                        .getDefaultVisibilityChecker()
                                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    return new JacksonConverter(mapper);
  }

  private RequestInterceptor getDefaultRequestInterceptor() {
    return new RequestInterceptor() {
      @Override
      public void intercept(RequestFacade request) {
        request.addHeader(Constants.AUTHORIZATION_HEADER, getEncodedAuthorization());
        request.addHeader(Constants.ACCEPT_HEADER, Constants.getAcceptHeaders());
      }
    };
  }

  /**
   * Gets the internal REST api adapter needed to complete Blockscore API requests.
   *
   * @return the REST adapter
   */
  public BlockscoreRestAdapter getAdapter() {
    return restAdapter;
  }
}
