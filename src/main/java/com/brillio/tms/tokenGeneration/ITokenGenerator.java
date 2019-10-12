package com.brillio.tms.tokenGeneration;

import com.brillio.tms.enums.TokenCategory;
import com.brillio.tms.models.Token;

public interface ITokenGenerator {
    Token generateToken(TokenCategory tokenCategory);
}
