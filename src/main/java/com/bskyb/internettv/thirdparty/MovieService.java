package com.bskyb.internettv.thirdparty;
import com.bskyb.internettv.thirdparty.TechnicalFailureException;
import com.bskyb.internettv.thirdparty.TitleNotFoundException;

public interface MovieService {
    String getParentalControlLevel(String movieId) throws TitleNotFoundException,
            TechnicalFailureException;
}
