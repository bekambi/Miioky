package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Util_User {
    public static User getUser(AppCompatActivity context, Map<Object, Object> objectMap, DataSnapshot ds) {
        User user = new User();

        user.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        user.setFullName(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_fullName))).toString());
        user.setUsername(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_username))).toString());
        user.setBirthDay(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_birthDay))).toString());
        user.setBio(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_bio))).toString());
        user.setProfileUrl(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_profileUrl))).toString());
        user.setFull_profileUrl(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_full_profileUrl))).toString());
        user.setPhoto_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photo_id))).toString());
        user.setPhoneNumber(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_phoneNumber))).toString());
        user.setStatus(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_status))).toString());
        user.setMarital_status(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_marital_status))).toString());
        user.setGender(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_gender))).toString());
        user.setTown_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_town_name))).toString());
        user.setNeighborhood_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_neighborhood_name))).toString());
        user.setNative_country_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_native_country_name))).toString());
        user.setLive_country_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_live_country_name))).toString());
        user.setHometown_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_hometown_name))).toString());
        user.setScope(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_scope))).toString());
        user.setSelected(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_isSelected))).toString()));
        user.setVerified(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_verified))).toString()));
        user.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));
        user.setViews(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_views))).toString()));

        List<SchoolAttended> schoolsList = new ArrayList<>();
        for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_school)).getChildren()){
            SchoolAttended school = new SchoolAttended();
            Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

            assert objectHashMap != null;
            school.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
            school.setSchoolKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_schoolKey))).toString());
            school.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
            school.setUser_school_attended(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended))).toString());
            school.setUser_school_attended_header(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended_header))).toString());

            schoolsList.add(school);
        }
        user.setSchool(schoolsList);

        List<FrequentedEstablishment> establishmentsList = new ArrayList<>();
        for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_establishments)).getChildren()) {
            FrequentedEstablishment establishment = new FrequentedEstablishment();
            establishment.setUser_establishment(Objects.requireNonNull(dSnapshot.getValue(FrequentedEstablishment.class)).getUser_establishment());
            establishment.setUser_establishment_header(Objects.requireNonNull(dSnapshot.getValue(FrequentedEstablishment.class)).getUser_establishment_header());
            establishmentsList.add(establishment);
        }
        user.setEstablishments(establishmentsList);

        List<WorkAt> workAtAtsList = new ArrayList<>();
        for (DataSnapshot dSnapshot : ds
                .child(context.getString(R.string.field_workAts)).getChildren()) {
            WorkAt workAt = new WorkAt();
            workAt.setUser_work_at(Objects.requireNonNull(dSnapshot.getValue(WorkAt.class)).getUser_work_at());
            workAt.setUser_work_at_header(Objects.requireNonNull(dSnapshot.getValue(WorkAt.class)).getUser_work_at_header());
            workAtAtsList.add(workAt);
        }
        user.setWorkAts(workAtAtsList);

        return user;
    }
}
