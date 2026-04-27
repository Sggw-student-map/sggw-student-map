package com.gwozdz1uuu.sggwstudentmap.settings;

record UpdateSettingsRequest(
    Boolean privateAccount
) {}

record UserSettingsResponse(
    boolean privateAccount,
    boolean gdprConsent
) {}