package com.github.pablowinck.pocrelatoriocsvspring;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class S3OutputStream extends OutputStream {

    private static final int PART_SIZE = 5 * 1024 * 1024; // 5 MB

    private final S3Client s3Client;

    private final String bucketName;

    private final String key;

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private final List<CompletedPart> completedParts = new ArrayList<>();

    private int partNumber = 1;

    private final String uploadId;

    public S3OutputStream(S3Client s3Client, String bucketName, String key) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.key = key;
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createMultipartUploadRequest);
        this.uploadId = response.uploadId();
    }

    @Override
    public void write(int b) {
        buffer.write(b);
        if (buffer.size() >= PART_SIZE) {
            uploadPart();
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        buffer.write(b, off, len);
        if (buffer.size() >= PART_SIZE) {
            uploadPart();
        }
    }

    private void uploadPart() {
        byte[] bytes = buffer.toByteArray();
        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .partNumber(partNumber++)
                .contentLength((long) bytes.length)
                .build();
        UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest,
                RequestBody.fromBytes(bytes));
        completedParts.add(CompletedPart.builder()
                .partNumber(partNumber - 1)
                .eTag(uploadPartResponse.eTag())
                .build());
        buffer.reset();
    }

    public void completeUpload() {
        if (buffer.size() > 0) {
            uploadPart();
        }
        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                .build();
        s3Client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    @Override
    public void close() throws IOException {
        completeUpload();
        buffer.close();
    }

}

