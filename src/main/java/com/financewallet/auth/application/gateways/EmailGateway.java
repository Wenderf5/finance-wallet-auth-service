package com.financewallet.auth.application.gateways;

import com.financewallet.auth.application.dto.Email;

public interface EmailGateway {
    void send(Email email);
}
