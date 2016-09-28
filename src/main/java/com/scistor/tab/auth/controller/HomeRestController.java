package com.scistor.tab.auth.controller;

import com.scistor.tab.auth.licensing.LicenseValidator;
import com.scistor.tab.auth.model.KeyValue;
import com.scistor.tab.auth.model.User;
import com.scistor.tab.auth.repository.KeyValueRepository;
import com.scistor.tab.auth.repository.UserRepository;
import net.nicholaswilliams.java.licensing.License;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Wei Xing
 */
@RestController
public class HomeRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeyValueRepository keyValueRepository;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public User getUserInfo(HttpServletRequest request) {
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        return userRepository.findByUsername(request.getUserPrincipal().getName());
    }

    @RequestMapping(value = "/license", method = RequestMethod.POST, consumes = "text/plain")
    public void postLicense(HttpServletRequest request) throws IOException {
        int size = request.getContentLength();
        if (size > KeyValue.MAX_SIZE) {

        }
        byte[] data = IOUtils.toByteArray(request.getInputStream(), size);
        License license = new LicenseValidator().decryptAndVerifyLicense(request.getInputStream());
        keyValueRepository.save(new KeyValue("license", data));
    }

}
