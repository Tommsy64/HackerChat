package io.github.tommsy64.netchat.user;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message implements Serializable {
    private static final long serialVersionUID = -4966004797688742079L;

    private String data;
    private Date sentTime;
}
