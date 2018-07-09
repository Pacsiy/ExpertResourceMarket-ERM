package edu.buaa.server.serviceInterface.resourceService;

import edu.buaa.server.dataLayer.domain.Comment;
import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.serviceImplement.EnumType.Role;
import edu.buaa.server.util.IntegerWrapper;

import java.util.List;

public interface CommonResourceServiceIF {
    Resource getResourceByID(Role role, String resID);

    Object getDetailsByIDAndType(Role role, String resID, String type);

    Object getDetailsByID(Role role, String resID);

    List<Comment> getCommentsByID(Role role, String resID, String index, String size, IntegerWrapper pageNum);
}
