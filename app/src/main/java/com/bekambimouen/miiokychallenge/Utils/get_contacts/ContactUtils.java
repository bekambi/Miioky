package com.bekambimouen.miiokychallenge.Utils.get_contacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;

import java.util.ArrayList;

public class ContactUtils {
    private static AppCompatActivity mContext;

    @SuppressLint("Range")
    public static ArrayList<String> getContacts(AppCompatActivity context) {
        mContext = context;
        ArrayList<String> phoneContacts_list = new ArrayList<>();
        ArrayList<String> contacts_list = new ArrayList<>();
        // this method is use to read contact from users device.
        // on below line we are creating a string variables for
        // our contact id and display name.
        String contactId = "";
        // on below line we are calling our content resolver for getting contacts
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        // on blow line we are checking the count for our cursor.
        if (cursor.getCount() > 0) {
            // if the count is greater than 0 then we are running a loop to move our cursor to next.
            while (cursor.moveToNext()) {
                // on below line we are getting the phone number.
                @SuppressLint("Range") int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    // we are checking if the has phone number is > 0
                    // on below line we are getting our contact id and user name for that contact
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    // on below line we are calling a content resolver and making a query
                    Cursor phoneCursor = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);
                    // on below line we are moving our cursor to next position.
                    if (phoneCursor.moveToNext()) {
                        // on below line we are getting the phone number for our users and then adding the name along with phone number in array list.
                        @SuppressLint("Range") String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneContacts_list.add(phoneNumber);
                    }
                    // on below line we are closing our phone cursor.
                    phoneCursor.close();
                }
            }
        }
        // on below line we are closing our cursor.
        cursor.close();

        for (int i = 0; i < phoneContacts_list.size(); i++) {
            contacts_list.add(phoneContacts_list.get(i).trim().replace(GetCountryZipCode(), "")
                    .replace("-", "").replace("+", ""));
        }

        return contacts_list;
    }

    // country name corresponding to country code
    private static String GetCountryZipCode(){
        String CountryID;
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = mContext.getResources().getStringArray(R.array.CountryCodes);
        for (String s : rl) {
            String[] g = s.split(mContext.getString(R.string.coma));
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }
}
