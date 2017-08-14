/*
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
package org.eurekaclinical.user.webapp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sun.jersey.api.client.ClientResponse.Status;
import javax.inject.Singleton;

import org.eurekaclinical.common.comm.clients.ClientException;
import org.eurekaclinical.user.client.EurekaClinicalUserClient;

import org.eurekaclinical.user.client.comm.LdapUserRequest;
import org.eurekaclinical.user.client.comm.LocalUserRequest;
import org.eurekaclinical.user.client.comm.OAuthUserRequest;
import org.eurekaclinical.user.client.comm.UserRequest;
import org.eurekaclinical.user.client.comm.authentication.AuthenticationMethod;

/**
 *
 * @author miaoai
 */
@Singleton
public class RegisterUserServlet extends HttpServlet {

    private static final ResourceBundle messages = ResourceBundle.getBundle("Messages");
    private static final long serialVersionUID = 1L;
    private final Injector injector;

    @Inject
    public RegisterUserServlet(Injector inInjector) {
        this.injector = inInjector;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String authenticationMethodStr = req.getParameter("authenticationMethod");
        AuthenticationMethod authenticationMethod;
        try {
            authenticationMethod = AuthenticationMethod.valueOf(authenticationMethodStr);
        } catch (IllegalArgumentException ex) {
            throw new ServletException("Invalid authentication method: " + authenticationMethodStr);
        }
        EurekaClinicalUserClient servicesClient
                = this.injector.getInstance(EurekaClinicalUserClient.class);
        try {
            String username = req.getParameter("username");
            String email = req.getParameter("email");
            String verifyEmail = req.getParameter("verifyEmail");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String organization = req.getParameter("organization");
            String title = req.getParameter("title");
            String department = req.getParameter("department");
            String fullName = req.getParameter("fullName");
            UserRequest userRequest;
            try {
                switch (authenticationMethod) {
                    case LOCAL:
                        String password = req.getParameter("password");
                        String verifyPassword = req.getParameter("verifyPassword");
                        LocalUserRequest localUserRequest = new LocalUserRequest();
                        localUserRequest.setVerifyPassword(verifyPassword);
                        localUserRequest.setPassword(password);
                        localUserRequest.setUsername(email);
                        userRequest = localUserRequest;
                        break;
                    case LDAP:
                        userRequest = new LdapUserRequest();
                        userRequest.setUsername(username);
                        break;
                    case OAUTH:
                        String providerUsername = req.getParameter("providerUsername");
                        String oauthProvider = req.getParameter("oauthProvider");
                        OAuthUserRequest oauthUserRequest = new OAuthUserRequest();
                        oauthUserRequest.setUsername(username);
                        oauthUserRequest.setProviderUsername(providerUsername);
                        oauthUserRequest.setOAuthProvider(oauthProvider);
                        userRequest = oauthUserRequest;
                        break;
                    default:
                        throw new ServletException("Unexpected authentication method: "
                                + authenticationMethod);
                }
            } catch (IllegalArgumentException iae) {
                throw new ServletException("Invalid authentication method: "
                        + authenticationMethod);
            }

            if (fullName == null || fullName.trim().length() == 0) {
                fullName = MessageFormat.format(messages.getString("registerUserServlet.fullName"), new Object[]{firstName, lastName});
            }

            userRequest.setFirstName(firstName);
            userRequest.setLastName(lastName);
            userRequest.setEmail(email);
            userRequest.setVerifyEmail(verifyEmail);
            userRequest.setOrganization(organization);
            userRequest.setTitle(title);
            userRequest.setDepartment(department);
            userRequest.setFullName(fullName);

            servicesClient.addUser(userRequest);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (ClientException e) {
            String msg = e.getMessage();
            Status responseStatus = e.getResponseStatus();
            switch (responseStatus) {
                case CONFLICT:
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    msg = messages.getString("registerUserServlet.error.conflict");
                    break;
                case BAD_REQUEST:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    msg = messages.getString("registerUserServlet.error.badRequest");
                    break;
                default:
                    throw new ServletException();
            }
            resp.setContentType("text/plain");
            resp.setContentLength(msg.length());
            resp.getWriter().write(msg);

        }

    }
}
