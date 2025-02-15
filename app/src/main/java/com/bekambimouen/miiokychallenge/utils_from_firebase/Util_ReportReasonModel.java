package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.manage_member.report_post.model.ReportReasonModel;

import java.util.Map;
import java.util.Objects;

public class Util_ReportReasonModel {
    public static ReportReasonModel getReportReasonModel(AppCompatActivity context, Map<Object, Object> objectMap) {
        ReportReasonModel reasonModel = new ReportReasonModel();

        reasonModel.setReason_of_report(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reason_of_report))).toString());
        reasonModel.setNumber_of_report(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_number_of_report))).toString());
        reasonModel.setReason_of_report(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reason_of_report))).toString());
        reasonModel.setPhoto_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photo_id))).toString());

        return reasonModel;
    }
}

