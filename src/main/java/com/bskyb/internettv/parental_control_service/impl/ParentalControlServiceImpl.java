package com.bskyb.internettv.parental_control_service.impl;

import com.bskyb.internettv.parental_control_service.ParentalControlService;
import com.bskyb.internettv.thirdparty.MovieService;
import com.bskyb.internettv.thirdparty.TechnicalFailureException;
import com.bskyb.internettv.thirdparty.TitleNotFoundException;
import com.bskyb.internettv.enums.ParentalControlLevel;
public class ParentalControlServiceImpl implements ParentalControlService {
    private MovieService movieService;

    public ParentalControlServiceImpl(MovieService movieService) {
        this.movieService = movieService;
    }

   
    public boolean canWatchMovie(String customerParentalControlLevel, String movieId) throws TitleNotFoundException {
    	 if (!validate(movieId, customerParentalControlLevel)) {
             return false;
         }
         ParentalControlLevel movieControlLevel = null;
         try {
             movieControlLevel = getMovieParentalControlLevel(movieId);
         } catch (TechnicalFailureException e) {
             return false;
         }
         int diff = ParentalControlLevel.valueOf(customerParentalControlLevel).compareTo(movieControlLevel);
         return diff > 0;
	}

    private ParentalControlLevel getMovieParentalControlLevel(String movieId) throws TitleNotFoundException, TechnicalFailureException {
        String movieLevelStr = null;
        try {
            movieLevelStr = movieService.getParentalControlLevel(movieId);
        } catch (TechnicalFailureException e) {
            throw e;
        }
        if (!validateParentalControlLevel(movieLevelStr)) {
            throw new TechnicalFailureException("The returned parental control level from the movie service is not valid.");
        } else {
            return ParentalControlLevel.valueOf(movieLevelStr);
        }
    }
    private boolean validate(String movieId, String customerParentalControlLevel) {
        if (movieId == null || movieService == null) {
            return false;
        }
        return validateParentalControlLevel(customerParentalControlLevel);
    }

    private boolean validateParentalControlLevel(String parentalControlLevel) {
        try {
            ParentalControlLevel.valueOf(parentalControlLevel);
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }




	
}

