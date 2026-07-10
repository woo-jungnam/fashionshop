package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.SePayWebhookDto;

public interface PaymentService {
    void processWebhook(SePayWebhookDto webhook);
}
