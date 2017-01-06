package com.gvozditskiy.watermeter.database;

/**
 * Created by Alexey on 23.12.2016.
 */

public class DbSchema {
    public static final class UserTable {
        public static final String NAME="user";

        public static class Cols {
            public static final String SECONDNAME = "second_name";
            public static final String FIRSTNAME = "first_name";
            public static final String PATRONYMIC = "patronymic";
            public static final String STREET = "street";
            public static final String STREET_TYPE = "street_type";
            public static final String BUILDING = "building";
            public static final String FLAT = "flat";
            public static final String FLAT_UUID = "flat_UUID";
            public static final String PHONE = "phone";
        }
    }

    public static final class IndTable {
        public static final String NAME = "indicators";

        public static class Cols {
            public static final String UUID = "uuid";
            public static final String YEAR = "year";
            public static final String MONTH = "month";
            public static final String VALUE = "value";
            public static final String METER_UUID = "meter_uuid";
        }
    }

    public  static final class FlatsTable {
        public static final String NAME = "flats";

        public static class Cols {
            public static final String NAME = "name";
            public static final String UUID = "uuid";
        }
    }

    public static final class MeterTable {
        public static final String NAME = "meters";

        public static   class Cols {
            public static final String TYPE = "type";
            public static final String NAME = "name";
            public static final String UUID = "uuid";
            public static final String FLAT_UUID = "flat_uuid";
        }

    }
}
