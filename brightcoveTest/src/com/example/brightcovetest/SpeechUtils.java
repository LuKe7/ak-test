package com.example.brightcovetest;

/**
 * Created by dx068-xl on 13-06-20.
 */
public class SpeechUtils {

    private SpeechUtils(){}

    public static enum SpeechCmd {
        SEARCH{
            @Override
            public boolean matchesCmd(String speechInput){
                return speechInput.contains("search");
            }
        },
        TOGGLE_MUTE{
            @Override
            public boolean matchesCmd(String speechInput) {
                return speechInput.contains("mute");
            }
        },
        CHANGE_VOLUME{
            @Override
            public boolean matchesCmd(String speechInput) {
                return speechInput.contains("volume");
            }
        },
        VOLUME_UP{
            @Override
            public boolean matchesCmd(String speechInput) {
                return speechInput.contains("volume up")|| speechInput.contains("volumeup");
            }
        },
        VOLUME_DOWN{
            @Override
            public boolean matchesCmd(String speechInput) {
                return speechInput.contains("volume down")|| speechInput.contains("volumedown");
            }
        },
        LANDSCAPE{
            @Override
            public boolean matchesCmd(String speechInput) {
                return speechInput.contains("landscape");
            }
        },
        PORTRAIT{
            @Override
            public boolean matchesCmd(String speechInput) {
                return speechInput.contains("portrait");
            }
        },
        SCREEN_DEFAULT{
            @Override
            public boolean matchesCmd(String speechInput) {
                return (speechInput.contains("screen") || speechInput.contains("orientation"))
                        && (speechInput.contains("all") || speechInput.contains("default"));
            }
        },
        UNKNOWN{};

        public boolean matchesCmd(String speechInput){
            return false;
        }

        public static SpeechCmd getSpeechCmd(String speechInput) {

           if(SEARCH.matchesCmd(speechInput)){
               return SEARCH;

            }else if(TOGGLE_MUTE.matchesCmd(speechInput)){
               return TOGGLE_MUTE;

           }else if (CHANGE_VOLUME.matchesCmd(speechInput)){
               if(VOLUME_DOWN.matchesCmd(speechInput)){
                   return VOLUME_DOWN;

               }else if(VOLUME_UP.matchesCmd(speechInput)){
                   return VOLUME_UP;
               }

               return CHANGE_VOLUME;

           } else if (LANDSCAPE.matchesCmd(speechInput)) {
               return LANDSCAPE;

           } else if (PORTRAIT.matchesCmd(speechInput)) {
               return PORTRAIT;

           } else if (SCREEN_DEFAULT.matchesCmd(speechInput)) {
               return SCREEN_DEFAULT;
           }

            return UNKNOWN;
        }
    }
}
