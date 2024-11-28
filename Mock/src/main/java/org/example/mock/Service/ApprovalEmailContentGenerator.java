package org.example.mock.Service;

import org.example.mock.Model.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class ApprovalEmailContentGenerator {

    @Autowired
    private TemplateEngine templateEngine;

    public String generateApprovalEmailContent(Offer offer) {
        // Tạo context để truyền dữ liệu vào template
        Context context = new Context();
        context.setVariable("offerDate", offer.getOfferDate());
        context.setVariable("startDate", offer.getStartDate());
        context.setVariable("salary", offer.getSalary());
        context.setVariable("candidateName", offer.getCandidate().getName());
        context.setVariable("createdBy", offer.getCreatedUser().getName());

        // Render template thành HTML string
        return templateEngine.process("Manager/offerForm", context);
    }
}
