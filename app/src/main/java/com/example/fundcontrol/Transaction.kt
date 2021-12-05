package com.example.fundcontrol

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Transaction(val id: Int,          // идентификатор
                  val date: String,     // дата создания транзакции
                  val category: String, // категория
                  val value: Int,       // сумма (>0 начисления, <0 траты)
                  val note: String      // описание
                   ) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(date)
        parcel.writeString(category)
        parcel.writeInt(value)
        parcel.writeString(note)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Transaction> {
        override fun createFromParcel(parcel: Parcel): Transaction {
            return Transaction(parcel)
        }

        override fun newArray(size: Int): Array<Transaction?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "id: $id\ndate: $date\ncategory: $category\nvalue: $value\nnote: $note"
    }

}