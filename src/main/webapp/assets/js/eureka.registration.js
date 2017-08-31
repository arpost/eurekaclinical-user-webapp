/*-
 * #%L
 * Eureka! Clinical User Webapp
 * %%
 * Copyright (C) 2016 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
// get the namespace, or declare it here
window.eureka = (typeof window.eureka == "undefined" || !window.eureka) ? {} : window.eureka;

// add in the namespace
window.eureka.registration = new function () {
    var self = this;

    self.setup = function (registrationForm, agreementAnchor, agreementModal) {
        $(registrationForm).submit(this.submit);
        $(agreementAnchor).click(function () {
            $(agreementModal).modal('show');
        });
        $.validator.addMethod("passwordCheck",
                function (value, element) {
                    return value.length >= 8 && /[0-9]+/.test(value) && /[a-zA-Z]+/.test(value);
                },
                "Please enter at least 8 characters with at least 1 digit."
                );
    };

    self.validator = $("#signupForm").validate({
        errorElement: 'span',
        rules: {
            firstName: "required",
            lastName: "required",
            password: {
                required: true,
                minlength: 8,
                passwordCheck: true
            },
            organization: {
                required: true
            },
            verifyPassword: {
                required: true,
                equalTo: "#password"
            },
            email: {
                required: true,
                email: true
            },
            verifyEmail: {
                required: true,
                email: true,
                equalTo: "#email"
            },
            agreement: {
                required: true
            },
            title: {
                required: true
            },
            department: {
                required: true
            }
        },
        messages: {
            firstName: "Enter your firstname",
            lastName: "Enter your lastname",
            organization: "Enter your organization",
            password: {
                required: "Provide a password",
                rangelength: $.validator.format("Please enter at least {0} characters")
            },
            verifyPassword: {
                required: "Repeat your password",
                minlength: $.validator.format("Please enter at least {0} characters"),
                equalTo: "Enter the same password as above"
            },
            email: {
                required: "Please enter a valid email address",
                minlength: "Please enter a valid email address"
            },
            verifyEmail: {
                required: "Please enter a valid email address",
                minlength: "Please enter a valid email address",
                equalTo: "Enter the same email as above"
            },
            agreement: {
                required: "Please check the agreement checkbox"
            },
            title: "Enter your title",
            department: "Enter your department"
        },
        errorPlacement: function (error, element) {
            $(element).closest('.form-group').find('.help-block').empty();
            error.appendTo($(element).closest('.form-group').find('.help-block'));
        },
        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
            $(element).closest('.form-group').find('.help-block').removeClass('default-hidden');
        },
        unhighlight: function (element) {
            $(element).closest('.form-group').removeClass('has-error');
            $(element).closest('.form-group').find('.help-block').addClass('default-hidden');
        },
        // set this class to error-labels to indicate valid fields
        success: function (label) {
            // set &nbsp; as text for IE
            label.html("&nbsp;").addClass("checked");
        }
    });

    self.submit = function () {
        if (self.validator.valid()) {
            var username = $('#username').val();
            var firstName = $('#firstName').val();
            var lastName = $('#lastName').val();
            var organization = $('#organization').val();
            var password = $('#password').val();
            var verifyPassword = $('#verifyPassword').val();
            var email = $('#email').val();
            var verifyEmail = $('#verifyEmail').val();
            var title = $('#title').val();
            var department = $('#department').val();
            var providerUsername = $('#providerUsername').val();
            var authenticationMethod = $('#authenticationMethod').val();
            var oauthProvider = $('#oauthProvider').val();

            var dataString = 'firstName=' + firstName + '&lastName=' + lastName + '&organization=' + organization +
                    '&password=' + password + '&verifyPassword=' + verifyPassword +
                    '&email=' + email + '&verifyEmail=' + verifyEmail + '&title=' + title + '&department=' + department +
                    (username ? '&username=' + username : '') +
                    (providerUsername ? '&providerUsername=' + providerUsername : '') +
                    '&authenticationMethod=' + authenticationMethod +
                    (oauthProvider ? '&oauthProvider=' + oauthProvider : '');
            $.ajax({
                type: 'POST',
                url: 'register',
                data: dataString,
                success: function () {
                    $('#passwordChangeFailure').hide();
                    $('#signupForm').hide();
                    $('#registerHeading').hide();
                    $('#registrationComplete').show();
                },
                error: function (xhr, err) {
                    $('#passwordChangeFailure').show();
                    if (xhr.status == "409") {
                        $('#passwordErrorMessage').html("Account already exists! You may go ahead and <a href='" + ctx + "'>login</a>.");
                    } else if (xhr.status == "400") {
                        $('#passwordErrorMessage').html("Please review your profile and complete all required fields.");
                    } else {
                        $('#passwordErrorMessage').html("An internal error has occurred. Please contact the technical team for further assistance.");
                    }

                }
            });
        }
        return false;
    }
};
