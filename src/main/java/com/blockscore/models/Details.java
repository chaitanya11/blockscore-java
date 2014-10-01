package com.blockscore.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Details breakdown model (Used in verification)
 * Created by Tony Dieppa on 9/29/14.
 */
public class Details {
    @Nullable
    @JsonProperty("address")
    private String mAddressMatch;

    @Nullable
    @JsonProperty("address_risk")
    private String mAddressRisk;

    @Nullable
    @JsonProperty("identification")
    private String mIdentficationMatch;

    @Nullable
    @JsonProperty("date_of_birth")
    private String mDateOfBirthMatch;

    @NotNull
    @JsonProperty("ofac")
    private String mOFACMatch;

    @Nullable
    @JsonProperty("entity_name")
    private String mEntityMatch;

    @Nullable
    @JsonProperty("tax_id")
    private String mTaxId;

    /**
     * Assesses the address match
     * @return Matching rating with the address.
     */
    @Nullable
    public MatchRank getAddressMatchDetails() {
        return getMatchRank(mAddressMatch);
    }

    /**
     * Assesses the entity match
     * @return Matching rating with the entity.
     */
    @Nullable
    public MatchRank getEntityMatch() {
        return getMatchRank(mEntityMatch);
    }

    /**
     * Assesses the tax ID match
     * @return Matching rating with the tax ID.
     */
    @Nullable
    public MatchRank getTaxIdMatch() {
        return getMatchRank(mTaxId);
    }

    /**
     * Gets the risk of this particular address.
     * @return Risk factor.
     */
    @Nullable
    public AddressRisk getAddressRisk() {
        if (mAddressRisk == null) {
            return null;
        }
        return AddressRisk.valueOf(mAddressRisk.toLowerCase());
    }

    /**
     * Assesses the match for identity.
     * @return Identity match rating.
     */
    @Nullable
    public MatchRank getIdentfication() {
        return getMatchRank(mIdentficationMatch);
    }

    /**
     * Assesses the date of birth match.
     * @return Date of birth match rating.
     */
    @Nullable
    public MatchRank getDateOfBirth() {
        return getMatchRank(mDateOfBirthMatch);
    }

    /**
     * Assesses the person for placement on the OFAC.
     * @return OFAC match rating.
     */
    @Nullable
    public MatchRank getOFAC() {
        return getMatchRank(mOFACMatch);
    }

    /**
     * Parses the string for the match rank.
     * @param matchResult String to parse.
     * @return Match rank.
     */
    @Nullable
    private MatchRank getMatchRank(final String matchResult) {
        try {
            return MatchRank.valueOf(matchResult.toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public enum MatchRank {
        MATCH("match"), NOMATCH("no_match"), PARTIAL_MATCH("partial_match"), MISMATCH("mismatch"),
        NO_DATA("insufficient_data");

        private final String mValue;

        private MatchRank(@NotNull final String value) {
            mValue = value;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }

    public enum AddressRisk {
        HIGH("high"), NOMATCH("no_match"), LOW("low"), NO_DATA("insufficient_data");

        private final String mValue;

        private AddressRisk(@NotNull final String value) {
            mValue = value;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }
}
