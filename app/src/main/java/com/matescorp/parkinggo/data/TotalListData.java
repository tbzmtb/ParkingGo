package com.matescorp.parkinggo.data;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by sjkim on 16. 5. 11.
 */
public class TotalListData {  // 아이콘


        // 제목
        public String mParking_message;

        // 날짜
        public String mTotal_date;

         public String lot_number;
//         public long header_layout_text;
         public String time;

//    public long getTime() {
//        return time;
//    }


    public String getTimeStringFormat() {

        SimpleDateFormat formatterY = new SimpleDateFormat("yyyy");
        String cYear = formatterY.format(new Date(System.currentTimeMillis()));
        String dYear = formatterY.format(time);

        SimpleDateFormat formatterM = new SimpleDateFormat("MM");
        String cMonth = formatterM.format(new Date(System.currentTimeMillis()));
        String dMonth = formatterM.format(time);

        SimpleDateFormat formatterD = new SimpleDateFormat("dd");
        String cDay = formatterD.format(new Date(System.currentTimeMillis()));
        String dDay = formatterD.format(time);


        return new SimpleDateFormat("HH:mm").format(new Date(time));

    }
        /**
         * 알파벳 이름으로 정렬
         */
//        public static final Comparator<TotalListData> ALPHA_COMPARATOR = new Comparator<TotalListData>() {
//            private final Collator sCollator = Collator.getInstance();
//
//            @Override
//            public int compare(TotalListData mListDate_1, TotalListData mListDate_2, TotalListData mListDate_3) {
//                return sCollator.compare(mListDate_1.mParking_message, mListDate_2.mParking_message, mListDate_3.mParking_message);
//            }
//        };
}
