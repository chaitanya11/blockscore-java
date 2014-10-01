package com.blockscore.net;

import com.blockscore.common.Constants;
import com.blockscore.exceptions.NoApiKeyFoundException;
import com.blockscore.models.*;
import com.blockscore.models.request.AnswerRequest;
import com.blockscore.models.request.QuestionSetRequest;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Request;
import retrofit.client.UrlConnectionClient;
import retrofit.converter.JacksonConverter;
import rx.Observable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The Blockscore Java API client.
 * Created by tealocean on 9/29/14.
 */
public class BlockscoreApiClient {
    private static RestAdapter.LogLevel sLogLevel = RestAdapter.LogLevel.NONE;
    private static String sApiKey;

    private BlockscoreRetrofitAPI restAdapter;

    /**
     * Initializes the API client with the API key. Must be done first or else all calls will fail.
     * @param apiKey API key to use.
     */
    public static void init(@NotNull final String apiKey) {
        sApiKey = apiKey + ":";
    }

    /**
     * Turns on/off logging. Should be done after init(), but before API client usage.
     */
    public static void useVerboseLogs(final boolean useVerboseLogs) {
        if (useVerboseLogs) {
            sLogLevel = RestAdapter.LogLevel.FULL;
        }
    }

    public BlockscoreApiClient() {
        RestAdapter.Builder restBuilder = new RestAdapter.Builder()
                .setClient(new BlockscoreClient())
                .setEndpoint(Constants.getDomain());

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        JacksonConverter converter = new JacksonConverter(mapper);
        restBuilder.setConverter(converter);

        //Sets up the authentication headers and accept headers.
        restBuilder.setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader(Constants.AUTHORIZATION_HEADER, getEncodedAuthorization());
                request.addHeader(Constants.ACCEPT_HEADER, Constants.getAcceptHeaders());
            }
        });

        restBuilder.setLogLevel(sLogLevel);
        restAdapter = restBuilder.build().create(BlockscoreRetrofitAPI.class);
    }

    /**
     * Creates a new verification.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#createVerification(com.blockscore.models.Person, retrofit.Callback)
     * @param person Person to verify.
     * @param callback Callback to use.
     */
    public void createVerification(@NotNull final Person person, @NotNull final Callback<Verification> callback) {
        restAdapter.createVerification(person, callback);
    }

    /**
     * Creates a new verification.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#createVerification(com.blockscore.models.Person)
     * @param person Person to verify.
     * @return Observable containing the verification results.
     */
    @NotNull
    public Observable<Verification> createVerification(@NotNull final Person person) {
        return restAdapter.createVerification(person);
    }

    /**
     * Pulls up a single verification.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#getVerification(String, retrofit.Callback)
     * @param id ID of verification to verify.
     * @param callback Callback to use.
     */
    public void getVerification(@NotNull final String id, final Callback<Verification> callback) {
        restAdapter.getVerification(id, callback);
    }

    /**
     * Pulls up a single verification.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#getVerification(String)
     * @param id ID of verification to verify.
     * @return Observable containing the verification results.
     */
    @NotNull
    public Observable<Verification> getVerification(@NotNull final String id) {
        return restAdapter.getVerification(id);
    }

    /**
     * Gets a list of verifications.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#listVerifications(retrofit.Callback)
     * @param callback Callback to use.
     */
    public void listVerifications(@NotNull final Callback<List<Verification>> callback) {
        restAdapter.listVerifications(callback);
    }

    /**
     * Gets a list of verifications.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#listVerifications()
     * @return Observable containing the list of verification results.
     */
    @NotNull
    public Observable<List<Verification>> listVerifications() {
        return restAdapter.listVerifications();
    }

    /**
     * Creates a question set.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#createQuestionSet(com.blockscore.models.request.QuestionSetRequest, retrofit.Callback)
     * @param request Question set request.
     * @param callback Callback to use.
     */
    public void createQuestionSet(@NotNull final QuestionSetRequest request
            , @NotNull final Callback<QuestionSet> callback) {
        restAdapter.createQuestionSet(request, callback);
    }

    /**
     * Creates a question set.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#createQuestionSet(com.blockscore.models.request.QuestionSetRequest)
     * @param request Question set request.
     * @return Observable containing the question set.
     */
    @NotNull
    public Observable<QuestionSet> createQuestionSet(@NotNull final QuestionSetRequest request) {
        return restAdapter.createQuestionSet(request);
    }

    /**
     * Scores a question set.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#scoreQuestionSet(String, com.blockscore.models.request.AnswerRequest, retrofit.Callback)
     * @param questionSetId Question set ID
     * @param answers Answers to questions
     * @param callback Callback to use.
     */
    public void scoreQuestionSet(@NotNull final String questionSetId
            , @NotNull final AnswerRequest answers
            , @NotNull final Callback<QuestionSet> callback) {
        restAdapter.scoreQuestionSet(questionSetId, answers, callback);
    }

    /**
     * Scores a question set.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#scoreQuestionSet(String, com.blockscore.models.request.AnswerRequest)
     * @param questionSetId Question set ID
     * @param answers Answers to questions
     */
    @NotNull
    public Observable<QuestionSet> scoreQuestionSet(@NotNull final String questionSetId
            , @NotNull final AnswerRequest answers) {
        return restAdapter.scoreQuestionSet(questionSetId, answers);
    }

    /**
     * This allows you to retrieve a question set you have created.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#getQuestionSet(String, retrofit.Callback)
     * @param questionSetId Question set ID
     * @param callback Callback to use.
     */
    public void getQuestionSet(@NotNull final String questionSetId
            , @NotNull final Callback<QuestionSet> callback) {
        restAdapter.getQuestionSet(questionSetId, callback);
    }

    /**
     * This allows you to retrieve a question set you have created.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#getQuestionSet(String)
     * @param questionSetId Question set ID
     * @return Observable containing the question set.
     */
    public Observable<QuestionSet> getQuestionSet(@NotNull final String questionSetId) {
        return restAdapter.getQuestionSet(questionSetId);
    }

    /**
     * This allows you to retrieve a question set you have created.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#listQuestionSets(retrofit.Callback)
     * @param callback Callback to use.
     */
    public void listQuestionSet(@NotNull final Callback<List<QuestionSet>> callback) {
        restAdapter.listQuestionSets(callback);
    }

    /**
     * This allows you to retrieve a question set you have created.
     * @see BlockscoreRetrofitAPI#listQuestionSets()
     * @return Observable containing the question set.
     */
    @NotNull
    public Observable<List<QuestionSet>> listQuestionSet() {
        return restAdapter.listQuestionSets();
    }

    /**
     * Creates a company.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#createCompany(com.blockscore.models.Company, retrofit.Callback)
     * @param company Company to create.
     * @param callback Callback to use.
     */
    public void createCompany(@NotNull final Company company, @NotNull final Callback<Company> callback) {
        restAdapter.createCompany(company, callback);
    }

    /**
     * Creates a company.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#createCompany(com.blockscore.models.Company)
     * @param company Company to create.
     * @return Observable containing the company.
     */
    @NotNull
    public Observable<Company> createCompany(@NotNull final Company company) {
        return restAdapter.createCompany(company);
    }

    /**
     * Retrieves a company.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#createCompany(com.blockscore.models.Company, retrofit.Callback)
     * @param id Company ID.
     * @param callback Callback to use.
     */
    public void getCompany(@NotNull final String id, @NotNull final Callback<Company> callback) {
        restAdapter.getCompany(id, callback);
    }

    /**
     * Retrieves a company.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#createCompany(com.blockscore.models.Company)
     * @param id Company ID.
     * @return Observable containing the company.
     */
    @NotNull
    public Observable<Company> getCompany(@NotNull final String id) {
        return restAdapter.getCompany(id);
    }

    /**
     * Lists your verified companies.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#listCompanies(retrofit.Callback)
     * @param callback Callback to use.
     */
    public void listCompanies(@NotNull final Callback<List<Company>> callback) {
        restAdapter.listCompanies(callback);
    }

    /**
     * Lists your verified companies.
     * @see BlockscoreRetrofitAPI#listCompanies()
     * @return Observable containing the list of companies.
     */
    @NotNull
    public Observable<List<Company>> listCompanies() {
        return restAdapter.listCompanies();
    }

    /**
     * Creates a watchlist candidate.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#createWatchlistCandidate(com.blockscore.models.WatchlistCandidate, retrofit.Callback)
     * @param candidate Watchlist candidate to create.
     * @param callback Callback to use.
     */
    public void createWatchlistCandidate(@NotNull final WatchlistCandidate candidate
            , @NotNull final Callback<WatchlistCandidate> callback) {
        restAdapter.createWatchlistCandidate(candidate, callback);
    }

    /**
     * Creates a watchlist candidate.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#createWatchlistCandidate(com.blockscore.models.WatchlistCandidate)
     * @param candidate Watchlist candidate to create.
     * @return Observable containing the candidate.
     */
    @NotNull
    public Observable<WatchlistCandidate> createWatchlistCandidate(@NotNull final WatchlistCandidate candidate) {
        return restAdapter.createWatchlistCandidate(candidate);
    }

    /**
     * Updates a watchlist candidate.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#updateWatchlistCandidate(String, com.blockscore.models.WatchlistCandidate, retrofit.Callback)
     * @param id ID for the candidate.
     * @param candidate Watchlist candidate to create.
     * @param callback Callback to use.
     */
    public void updateWatchlistCandidate(@NotNull final String id, @NotNull final WatchlistCandidate candidate
            , @NotNull final Callback<WatchlistCandidate> callback) {
        restAdapter.updateWatchlistCandidate(id, candidate, callback);
    }

    /**
     * Updates a watchlist candidate.
     * @see com.blockscore.net.BlockscoreRetrofitAPI#updateWatchlistCandidate(String, com.blockscore.models.WatchlistCandidate)
     * @param id ID for the candidate.
     * @param candidate Watchlist candidate to create.
     */
    @NotNull
    public Observable<WatchlistCandidate> updateWatchlistCandidate(@NotNull final String id
            , @NotNull final WatchlistCandidate candidate) {
        return restAdapter.updateWatchlistCandidate(id, candidate);
    }

    /**
     * Encodes the API key for Basic authentication.
     * @return API key encoded with Base 64.
     */
    @NotNull
    private String getEncodedAuthorization() {
        if (sApiKey == null || sApiKey.isEmpty()) {
            throw new NoApiKeyFoundException();
        }
        return "Basic " + Base64.getEncoder().encodeToString(sApiKey.getBytes());
    }

    /**
     * Handles the network layer.
     */
    private class BlockscoreClient extends UrlConnectionClient {
        private static final int CONNECT_TIMEOUT_MILLIS = 30 * 1000;
        private static final int READ_TIMEOUT_MILLIS = 30 * 1000;

        private final OkUrlFactory okUrlFactory;

        public BlockscoreClient() {
            okUrlFactory = new OkUrlFactory(generateHTTPClient());
        }

        public BlockscoreClient(OkHttpClient client) {
            okUrlFactory = new OkUrlFactory(client);
        }

        private OkHttpClient generateHTTPClient() {
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            return client;
        }

        @Override
        protected HttpURLConnection openConnection(Request request) throws IOException {
            return okUrlFactory.open(new URL(request.getUrl()));
        }
    }
}
