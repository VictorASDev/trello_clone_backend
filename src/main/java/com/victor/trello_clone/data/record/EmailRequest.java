package com.victor.trello_clone.data.record;

import java.io.File;

public record EmailRequest(String to, String subject, String body) {
}
