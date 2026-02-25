package com.victor.trello_clone.controller.docs;

import com.victor.trello_clone.data.record.EmailRequest;
import com.victor.trello_clone.data.record.TokenRequest;
import com.victor.trello_clone.data.record.ValidateEmailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Email Endpoint",
    description = "Endpoint for managing email-related operations such as sending, verification, and notifications!"
)
public interface EmailControllerDocs {

    @Operation(
            summary = "Send an simple Email",
            description = "It asks for the recipient's email, the subject, and the body of the email and sends it!",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
     ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest);

    @Operation(
            summary = "Send an Email with attachment!",
            description = "It asks for the recipient's email, the subject, the body and the attachment of the email and sends it!",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
     ResponseEntity<String> sendEmailWithAttachment(String emailRequestJson, MultipartFile file);

    @Operation(
            summary = "Send an Email to validate the account!",
            description = "Requests the user's email and sends an email containing a verification token!",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
     ResponseEntity<?> sendVerification(@RequestBody ValidateEmailRequest req);

    @Operation(
            summary = "validate the account verification token!",
            description = "Receives a token, checks the necessary conditions, and validates the account!",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
     ResponseEntity<Void> validateEmailToken(@RequestBody TokenRequest request);
}