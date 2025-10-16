package com.android_assignments.expensetrackerpro_maan.models;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.UUID;

public class Transaction implements Parcelable {
    public String id;
    public String title;
    public String category;
    public double amount;
    public String date;
    public String description;
    public String receiptPath; // uri string
    public boolean isFavorite;

    public Transaction() {
        this.id = UUID.randomUUID().toString();
    }

    protected Transaction(Parcel in) {
        id = in.readString();
        title = in.readString();
        category = in.readString();
        amount = in.readDouble();
        date = in.readString();
        description = in.readString();
        receiptPath = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override public Transaction createFromParcel(Parcel in) { return new Transaction(in); }
        @Override public Transaction[] newArray(int size) { return new Transaction[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(category);
        dest.writeDouble(amount);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(receiptPath);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}
