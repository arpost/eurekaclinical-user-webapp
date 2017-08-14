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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eurekaclinical.common.comm.clients.ClientException;
import org.eurekaclinical.common.comm.Role;
import org.eurekaclinical.user.client.EurekaClinicalUserClient;

import org.eurekaclinical.user.client.comm.User;

import org.eurekaclinical.user.webapp.servlet.worker.ServletWorker;

/**
 *
 * @author miaoai
 */
public class ListUsersWorker implements ServletWorker {

    private final EurekaClinicalUserClient servicesClient;

    public ListUsersWorker(EurekaClinicalUserClient inServicesClient) {
        this.servicesClient = inServicesClient;
    }

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<User> users;
        try {
            users = this.servicesClient.getUsers();
        } catch (ClientException ex) {
            throw new ServletException("Error getting user list", ex);
        }
        List<Role> roles;
        try {
            roles = this.servicesClient.getRoles();
        } catch (ClientException ex) {
            throw new ServletException("Error getting role list", ex);
        }
        Map<Long, Role> rolesMap = new HashMap<>();
        for (Role role : roles) {
            rolesMap.put(role.getId(), role);
        }

        // Set sort order to show the inactive users first.
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                int u1 = 0;
                int u2 = 0;
                if (user1.isActive()) {
                    u1 = 1;
                }
                if (user2.isActive()) {
                    u2 = 1;
                }

                return u1 - u2;
            }
        });
        req.setAttribute("users", users);
        req.setAttribute("roles", rolesMap);
        req.getRequestDispatcher("/protected/admin.jsp").forward(req, resp);
    }
}
