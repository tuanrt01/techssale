package com.techzen.techsale.service;

import java.io.IOException;

public interface AttachmentsService {

    byte[] getAttachments(int fileId) throws IOException;
}
