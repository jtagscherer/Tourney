package usspg31.tourney.controller.util;

import usspg31.tourney.model.Player;

/**
 * Helper class for fuzzy searching
 * 
 * @author Jan Tagscherer
 */
public class SearchUtilities {
    /**
     * Get the search relevance of the input string
     * 
     * @param input
     *            Content string
     * @param filter
     *            Search term
     * @return The search relevance of these two strings, the lower it is the
     *         more relevant the input
     */
    public static double getSearchRelevance(String input, String filter) {
        String shortenedInput = input;
        if (filter.length() < shortenedInput.length()) {
            shortenedInput = input.substring(0, filter.length());
        }

        return SearchUtilities.calculateDistance(shortenedInput, filter);
    }

    /**
     * Get the search relevance of the input player. This will return the
     * minimum relevance after iterating over all searchable fields.
     * 
     * @param input
     *            Content player
     * @param filter
     *            Search term
     * @return The search relevance of the two values
     */
    public static double getSearchRelevance(Player input, String filter) {
        String[] attributes = { input.getFirstName(), input.getLastName(),
                input.getNickName(), input.getMailAddress() };
        double minimumRelevance = Double.MAX_VALUE;

        for (String attribute : attributes) {
            double relevance = SearchUtilities.getSearchRelevance(attribute,
                    filter);
            if (relevance < minimumRelevance) {
                minimumRelevance = relevance;
            }
        }

        return minimumRelevance;
    }

    /**
     * Calculate the edit distance between two strings. This method calculates
     * the Levenshtein distance using dynamic programming.
     * 
     * @param firstString
     *            First given string
     * @param secondString
     *            Second given string
     * @return The edit distance between the two strings, the higher it is the
     *         more do they differ
     */
    private static int calculateDistance(String firstString, String secondString) {
        firstString = firstString.toLowerCase();
        secondString = secondString.toLowerCase();

        int[] costs = new int[secondString.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= firstString.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= secondString.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        firstString.charAt(i - 1) == secondString.charAt(j - 1)
                                ? nw
                                : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[secondString.length()];
    }
}
