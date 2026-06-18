package com.financewallet.auth.application.gateway;

import com.financewallet.auth.application.dto.Email;

public interface EmailGateway {
    void send(Email email);
}
