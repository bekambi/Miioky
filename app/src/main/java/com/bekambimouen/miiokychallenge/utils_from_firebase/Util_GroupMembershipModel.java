package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.model.GroupMembershipModel;

import java.util.Map;
import java.util.Objects;

public class Util_GroupMembershipModel {
    public static GroupMembershipModel getGroupMembershipModel(AppCompatActivity context, Map<Object, Object> objectMap) {
        GroupMembershipModel membershipModel = new GroupMembershipModel();

        membershipModel.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        membershipModel.setAdmin_master(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_admin_master))).toString());
        membershipModel.setGroup_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_id))).toString());
        membershipModel.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));

        return membershipModel;
    }
}

