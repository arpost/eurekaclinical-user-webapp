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
package org.eurekaclinical.user.webapp.servlet.worker.useracct;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;

import org.eurekaclinical.common.comm.clients.ClientException;
import org.eurekaclinical.user.client.EurekaClinicalUserProxyClient;

import org.eurekaclinical.user.webapp.servlet.worker.ServletWorker;
import org.eurekaclinical.user.webapp.config.UserWebappProperties;
/**
 *
 * @author miaoai
 */
public class SaveUserAcctWorker implements ServletWorker {

	private static Logger LOGGER = LoggerFactory.getLogger(SaveUserAcctWorker.class);
	
	private final ResourceBundle messages;
	private final EurekaClinicalUserProxyClient servicesClient;
	private final UserWebappProperties properties;

	public SaveUserAcctWorker(ServletContext ctx, EurekaClinicalUserProxyClient inClient) {
		String localizationContextName = ctx.getInitParameter("javax.servlet.jsp.jstl.fmt.localizationContext");
		this.messages = ResourceBundle.getBundle(localizationContextName);
		this.servicesClient = inClient;
		this.properties = new UserWebappProperties();
	}

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
                        resp.setContentType("text/html");                     
                        String oldPassword = req.getParameter("oldPassword");
                        String newPassword = req.getParameter("newPassword");
                        String verifyPassword = req.getParameter("verifyPassword");
                        if (!verifyPassword.equals(newPassword)) {
                                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                return;
                        }

                        try {
                                this.servicesClient.changePassword(oldPassword, newPassword);
                                resp.setStatus(HttpServletResponse.SC_OK);
                                resp.getWriter().write(HttpServletResponse.SC_OK);
                        } catch (ClientException e) {
                                LOGGER.error("Error trying to change password for user {}", req.getUserPrincipal().getName(), e);
                                resp.setContentType("text/plain");
                                if (ClientResponse.Status.PRECONDITION_FAILED.equals(e.getResponseStatus())) {
                                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                        resp.getWriter().write(e.getMessage());
                                } else {
                                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                        resp.getWriter().write(
                                                        messages.getString("passwordChange.error.internalServerError"));
                                }

                        }   
	}
}
