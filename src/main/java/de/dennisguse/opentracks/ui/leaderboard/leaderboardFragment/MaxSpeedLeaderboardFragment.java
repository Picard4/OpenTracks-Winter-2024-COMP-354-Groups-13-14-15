package de.dennisguse.opentracks.ui.leaderboard.leaderboardFragment;

import java.util.ArrayList;
import java.util.List;

import de.dennisguse.opentracks.data.models.Ranking;
import de.dennisguse.opentracks.ui.leaderboard.LeaderboardPagerAdapter;

public class MaxSpeedLeaderboardFragment extends LeaderboardFragment {
    private boolean averageRefresh;
    private boolean bestRefresh;

    @Override
    protected List<Ranking> calculateLatestAverageRankingsData(List<LeaderboardPagerAdapter.PlaceHolderTrackUser> latestLeaderboardData) {
        // TODO: Replace the test data with code that gathers the appropriate Ranking data
        List<Ranking> latestRankingsData;
        if (!averageRefresh)
            // Get a different data set if this is the first time the rankings data is being collected
            latestRankingsData = getTestData();
        else
            latestRankingsData = getAltTestData();
        // All future rankings data collections should be refreshes
        averageRefresh = true;
        return latestRankingsData;
    }

    @Override
    protected List<Ranking> calculateLatestBestRankingsData(List<LeaderboardPagerAdapter.PlaceHolderTrackUser> latestLeaderboardData) {
        // TODO: Replace the test data with code that gathers the appropriate Ranking data
        List<Ranking> latestRankingsData;
        if (!bestRefresh)
            // Get a different data set if this is the first time the rankings data is being collected
            latestRankingsData = getAltTestData();
        else
            latestRankingsData = getTestData();
        // All future rankings data collections should be refreshes
        bestRefresh = true;
        return latestRankingsData;
    }

    private List<Ranking> getTestData() {
        List<Ranking> rankings = new ArrayList<>();
        rankings.add(new Ranking(1, "User 10", "AA",  Double.toString(25)));
        rankings.add(new Ranking(2, "User 20", "BB",  Double.toString(24)));
        rankings.add(new Ranking(3, "User 30", "CC",  Double.toString(23)));
        rankings.add(new Ranking(4,  "User 40", "DD",  Double.toString(22)));
        rankings.add(new Ranking(5,  "User 50", "EE",  Double.toString(21)));
        rankings.add(new Ranking(6,  "User 60", "FF",  Double.toString(20)));
        rankings.add(new Ranking(7,  "User 70", "GG",  Double.toString(19)));
        rankings.add(new Ranking(8,  "User 80", "HH",  Double.toString(18)));
        rankings.add(new Ranking(9,  "User 90", "II",  Double.toString(17)));
        rankings.add(new Ranking(10,  "User 100", "JJ",  Double.toString(16)));
        rankings.add(new Ranking(11,  "User 110", "KK",  Double.toString(15)));
        rankings.add(new Ranking(12,  "User 120", "LL",  Double.toString(14)));
        rankings.add(new Ranking(13,  "User 130", "MM",  Double.toString(13)));
        rankings.add(new Ranking(14,  "User 140", "NN",  Double.toString(12)));
        rankings.add(new Ranking(15, "User 150", "OO",  Double.toString(11)));
        return rankings;
    }

    private List<Ranking> getAltTestData() {
        List<Ranking> rankings = new ArrayList<>();
        rankings.add(new Ranking(1, "Day One", "Steamboat Springs",  Double.toString(25)));
        rankings.add(new Ranking(2, "Day Two", "North California CA",  Double.toString(24)));
        rankings.add(new Ranking(3, "Day Three", "Steamboat Springs, Color Red",  Double.toString(23)));
        rankings.add(new Ranking(4,  "Day Four", "Montreal",  Double.toString(22)));
        return rankings;
    }
}