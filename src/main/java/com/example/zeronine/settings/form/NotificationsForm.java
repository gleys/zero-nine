package com.example.zeronine.settings.form;

import lombok.Data;

@Data
public class NotificationsForm {

    private boolean orderCreatedByEmail;

    private boolean orderCreatedByWeb;

    private boolean orderEnrollmentResultByEmail;

    private boolean orderEnrollmentResultByWeb;

    private boolean orderUpdatedByEmail;

    private boolean orderUpdatedByWeb;
}
