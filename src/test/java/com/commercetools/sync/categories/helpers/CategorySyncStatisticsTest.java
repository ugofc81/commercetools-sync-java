package com.commercetools.sync.categories.helpers;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.util.internal.StringUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.commercetools.sync.commons.MockUtils.getStatisticsAsJsonString;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class CategorySyncStatisticsTest {

    private CategorySyncStatistics categorySyncStatistics;

    @Before
    public void setup() {
        categorySyncStatistics = new CategorySyncStatistics();
    }

    @Test
    public void getUpdated_WithNoUpdated_ShouldReturnZero() {
        assertThat(categorySyncStatistics.getUpdated()).isEqualTo(0);
    }

    @Test
    public void incrementUpdated_ShouldIncrementUpdatedValue() {
        categorySyncStatistics.incrementUpdated();
        assertThat(categorySyncStatistics.getUpdated()).isEqualTo(1);
    }

    @Test
    public void getCreated_WithNoCreated_ShouldReturnZero() {
        assertThat(categorySyncStatistics.getCreated()).isEqualTo(0);
    }

    @Test
    public void incrementCreated_ShouldIncrementCreatedValue() {
        categorySyncStatistics.incrementCreated();
        assertThat(categorySyncStatistics.getCreated()).isEqualTo(1);
    }

    @Test
    public void getProcessed_WithNoProcessed_ShouldReturnZero() {
        assertThat(categorySyncStatistics.getProcessed()).isEqualTo(0);
    }

    @Test
    public void incrementProcessed_ShouldIncrementProcessedValue() {
        categorySyncStatistics.incrementProcessed();
        assertThat(categorySyncStatistics.getProcessed()).isEqualTo(1);
    }

    @Test
    public void getFailed_WithNoFailed_ShouldReturnZero() {
        assertThat(categorySyncStatistics.getFailed()).isEqualTo(0);
    }

    @Test
    public void incrementFailed_ShouldIncrementFailedValue() {
        categorySyncStatistics.incrementFailed();
        assertThat(categorySyncStatistics.getFailed()).isEqualTo(1);
    }

    @Test
    public void calculateProcessingTime_ShouldSetProcessingTimeInAllUnitsAndHumanReadableString() throws
        InterruptedException {
        assertThat(categorySyncStatistics.getLatestBatchProcessingTimeInMillis()).isEqualTo(0);
        assertThat(categorySyncStatistics.getLatestBatchHumanReadableProcessingTime())
            .isEqualTo(StringUtil.EMPTY_STRING);

        final int waitingTimeInMillis = 100;
        Thread.sleep(waitingTimeInMillis);
        categorySyncStatistics.calculateProcessingTime();

        assertThat(categorySyncStatistics.getLatestBatchProcessingTimeInDays()).isGreaterThanOrEqualTo(0);
        assertThat(categorySyncStatistics.getLatestBatchProcessingTimeInHours()).isGreaterThanOrEqualTo(0);
        assertThat(categorySyncStatistics.getLatestBatchProcessingTimeInMinutes()).isGreaterThanOrEqualTo(0);
        assertThat(categorySyncStatistics.getLatestBatchProcessingTimeInSeconds())
            .isGreaterThanOrEqualTo(waitingTimeInMillis / 1000);
        assertThat(categorySyncStatistics.getLatestBatchProcessingTimeInMillis())
            .isGreaterThanOrEqualTo(waitingTimeInMillis);

        final long remainingMillis = categorySyncStatistics.getLatestBatchProcessingTimeInMillis()
            - TimeUnit.SECONDS.toMillis(categorySyncStatistics.getLatestBatchProcessingTimeInSeconds());
        assertThat(categorySyncStatistics.getLatestBatchHumanReadableProcessingTime()).contains(format(", %dms",
            remainingMillis));
    }

    @Test
    public void getStatisticsAsJsonString_WithoutCalculatingProcessingTime_ShouldGetCorrectJsonString()
        throws JsonProcessingException {
        categorySyncStatistics.incrementCreated();
        categorySyncStatistics.incrementProcessed();

        categorySyncStatistics.incrementFailed();
        categorySyncStatistics.incrementProcessed();

        categorySyncStatistics.incrementUpdated();
        categorySyncStatistics.incrementProcessed();

        final String statisticsAsJsonString = getStatisticsAsJsonString(categorySyncStatistics);
        assertThat(statisticsAsJsonString)
                .isEqualTo("{\"reportMessage\":\"Summary: 3 categories were processed in total (1 created, 1 updated, "
                  + "1 failed to sync and 0 categories with a missing parent).\",\""
                    + "updated\":1,\""
                    + "created\":1,\""
                    + "failed\":1,\""
                    + "processed\":3,\""
                    + "latestBatchProcessingTimeInDays\":"
                    + categorySyncStatistics.getLatestBatchProcessingTimeInDays()
                    + ",\""
                    + "latestBatchProcessingTimeInHours\":"
                    + categorySyncStatistics.getLatestBatchProcessingTimeInHours()
                    + ",\"latestBatchProcessingTimeInMinutes\":"
                    + categorySyncStatistics.getLatestBatchProcessingTimeInMinutes()
                    + ",\"latestBatchProcessingTimeInSeconds\":"
                    + categorySyncStatistics.getLatestBatchProcessingTimeInSeconds()
                    + ",\"latestBatchProcessingTimeInMillis\":"
                    + categorySyncStatistics.getLatestBatchProcessingTimeInMillis()
                    + ",\"latestBatchHumanReadableProcessingTime\":\""
                    + categorySyncStatistics.getLatestBatchHumanReadableProcessingTime()
                    + "\",\"categoryKeysWithMissingParents\":"
                    + categorySyncStatistics.getCategoryKeysWithMissingParents() + "}");
    }

    @Test
    public void getStatisticsAsJsonString_WithCalculatingProcessingTime_ShouldGetCorrectJsonString()
        throws JsonProcessingException {
        categorySyncStatistics.incrementCreated();
        categorySyncStatistics.incrementProcessed();

        categorySyncStatistics.incrementFailed();
        categorySyncStatistics.incrementProcessed();

        categorySyncStatistics.incrementUpdated();
        categorySyncStatistics.incrementProcessed();

        categorySyncStatistics.calculateProcessingTime();

        final String statisticsAsJsonString = getStatisticsAsJsonString(categorySyncStatistics);
        assertThat(statisticsAsJsonString)
            .isEqualTo("{\"reportMessage\":\"Summary: 3 categories were processed in total (1 created, 1 updated, "
                + "1 failed to sync and 0 categories with a missing parent).\",\""
                + "updated\":1,\""
                + "created\":1,\""
                + "failed\":1,\""
                + "processed\":3,\""
                + "latestBatchProcessingTimeInDays\":"
                + categorySyncStatistics.getLatestBatchProcessingTimeInDays()
                + ",\""
                + "latestBatchProcessingTimeInHours\":"
                + categorySyncStatistics.getLatestBatchProcessingTimeInHours()
                + ",\"latestBatchProcessingTimeInMinutes\":"
                + categorySyncStatistics.getLatestBatchProcessingTimeInMinutes()
                + ",\"latestBatchProcessingTimeInSeconds\":"
                + categorySyncStatistics.getLatestBatchProcessingTimeInSeconds()
                + ",\"latestBatchProcessingTimeInMillis\":"
                + categorySyncStatistics.getLatestBatchProcessingTimeInMillis()
                + ",\"latestBatchHumanReadableProcessingTime\":\""
                + categorySyncStatistics.getLatestBatchHumanReadableProcessingTime()
                + "\",\"categoryKeysWithMissingParents\":"
                + categorySyncStatistics.getCategoryKeysWithMissingParents() + "}");
    }

    @Test
    public void getNumberOfCategoriesWithMissingParents_WithEmptyMap_ShouldReturn0() {
        final int numberOfCategoriesWithMissingParents = CategorySyncStatistics
            .getNumberOfCategoriesWithMissingParents(new HashMap<>());
        assertThat(numberOfCategoriesWithMissingParents).isZero();
    }

    @Test
    public void getNumberOfCategoriesWithMissingParents_WithNonEmptyMap_ShouldReturnCorrectNumberOfChildren() {
        final Map<String, ArrayList<String>> categoryKeysWithMissingParents = new HashMap<>();
        final ArrayList<String> firstMissingParentChildrenKeys = new ArrayList<>();
        firstMissingParentChildrenKeys.add("key1");
        firstMissingParentChildrenKeys.add("key2");

        final ArrayList<String> secondMissingParentChildrenKeys = new ArrayList<>();
        secondMissingParentChildrenKeys.add("key3");
        secondMissingParentChildrenKeys.add("key4");

        categoryKeysWithMissingParents.put("parent1", firstMissingParentChildrenKeys);
        categoryKeysWithMissingParents.put("parent2", secondMissingParentChildrenKeys);

        final int numberOfCategoriesWithMissingParents = CategorySyncStatistics
            .getNumberOfCategoriesWithMissingParents(categoryKeysWithMissingParents);
        assertThat(numberOfCategoriesWithMissingParents).isEqualTo(4);
    }
}
