package dissertation.adam.nfitnessc;


/**
 * Created by Adam on 17/02/2016.
 */
public class DbSchema {
    public static final class UserTable {
        public static final String NAME = "Users";

        public static final class Cols {
            public static String FIRSTNAME = "FirstName";
            public static String SURNAME = "Surname";
            public static String EMAIL = "Email";
            public static String PASSWORD = "Password";
        }
    }
    public static final class TreadmillTable {
        public static final String NAME = "Tread";

        public static final class Cols {
            public static final String EMAIL = "Email";
            public static final String DATE = "Date";
            public static final String TIME = "TimeTaken";
            public static final String DISTANCE = "Distance";
            public static final String SPEED = "Speed";
            public static final String CALORIES = "Calories";
            public static final String HEARTRATE = "HeartRate";

        }
    }
    public static final class ChestTable {
        public static final String NAME = "Chest";

        public static final class Cols {
            public static final String EMAIL = "Email";
            public static final String DATE = "Date";
            public static final String SETS = "Sets";
            public static final String REPS = "Reps";
            public static final String WEIGHTS = "Weights";
        }
    }
    public static final class BicepTable {
        public static final String NAME = "Bicep";

        public static final class Cols {
            public static final String EMAIL = "Email";
            public static final String DATE = "Date";
            public static final String SETS = "Sets";
            public static final String REPS = "Reps";
            public static final String WEIGHTS = "Weights";
        }
    }

    public static final class WeightTable {
        public static final String NAME = "Weights";

        public static final class Cols {
            public static final String EMAIL = "Email";
            public static final String DATE = "Date";
            public static final String WEIGHT = "Weight";
        }
    }
    public static final class GoalTable {
        public static final String NAME = "Goals";

        public static final class Cols {
            public static final String EMAIL = "Email";
            public static final String DATE = "Date";
            public static final String GOAL = "Goal";
        }
    }


}
