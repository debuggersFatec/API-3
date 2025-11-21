package com.api_3.api_3.model.embedded;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FileAttachment {

    private String originalName;
    private String storedName;
    private String uploaderUUID;
    private Date uploadedDate;
}