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
package org.eurekaclinical.user.webapp.servlet.worker.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eurekaclinical.common.comm.clients.ClientException;
import org.eurekaclinical.user.client.EurekaClinicalUserClient;

import org.eurekaclinical.user.client.comm.User;

import org.eurekaclinical.user.webapp.servlet.worker.ServletWorker;

/**
 *
 * @author miaoai
 */
public class SaveUserWorker implements ServletWorker {

    private final EurekaClinicalUserClient servicesClient;

    public SaveUserWorker(EurekaClinicalUserClient inServicesClient) {
        this.servicesClient = inServicesClient;
    }

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String id = req.getParameter("id");
        String activeStatus = req.getParameter("active");
        boolean isActivated = false;

        if (activeStatus != null) {
            isActivated = true;

        }
        try {
            User user = this.servicesClient.getUserById(Long.valueOf(id));
            String[] roles = req.getParameterValues("role");
            List<Long> userRoles = new ArrayList<>();
            if (roles != null) {
                for (String roleId : roles) {
                    try {
                        userRoles.add(Long.valueOf(roleId));
                    } catch (NumberFormatException nfe) {
                        throw new ServletException(nfe);
                    }
                }
            }
            user.setRoles(userRoles);
            user.setActive(isActivated);

            this.servicesClient.updateUser(user, Long.valueOf(id));
        } catch (ClientException e) {
            throw new ServletException("Error saving user", e);
        }

        resp.sendRedirect(req.getContextPath() + "/protected/admin?action=list");
    }
}
