/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;

public enum GENRES {
    FIFTIES                 ("50s"),
    SIXTIES                 ("60s"),
    SEVENTIES               ("70s"),
    EIGHTIES                ("80s"), 
    ROCKIN_EIGHTIES         ("80s_rr"), 
    NINTIES                 ("90s"),
    NINETIES_ALTERNATIVE    ("90s_aa"),
    ALTERNATIVE             ("aa"),
    THE_MIX                 ("ac"),
    AMERICANA               ("am"),
    AMBIENT                 ("amb"),
    AAA_BOULEVARD           ("ar"), 
    BIG_BAND_LAND           ("bgb"), 
    BIT_O_BLUES             ("bl"), 
    BLUEGRASS               ("blg"), 
    BRITISH_INVASION        ("br"),
    CLASSIC_COUNTRY         ("cc"),
    SPIRITUAL_SEASONS       ("ccx"), 
    CELTIC_CROSSINGS        ("cel"),
    CLASSIC_HITS            ("ch"),
    CHRISTIAN_CONTEMPORARY  ("chc"), 
    TOP_FORTY               ("chr"), 
    CLASSIC_ROCK            ("cr"),
    CONCERT_HALL            ("cl"),
    THE_BEAT                ("clb"),
    TODAYS_COUNTRY          ("ctr"), 
    COUNTRY_CHRISTMAS       ("ctrx"),
    CLASSICAL_VOICES        ("cv"), 
    DANCE                   ("da"),
    DISCO                   ("ds"),
    ELECTRONICA             ("el"), 
    FOLK_LORE               ("flk"),  
    GUITAR_GENIUS           ("gu"), 
    HOT_HITS                ("hac"),
    HALLOWEEN_PARTY         ("hal"), 
    HEAVENLY_HOLIDAYS       ("hh"),
    INDIE_UNDERGROUND       ("ir"),
    JAZZ_SO_TRUE            ("jz"), 
    LIBRARY                 ("li"),
    METAL_MADNESS           ("mr"), 
    MUSICAL_MAGIC           ("mu"), 
    MASH_UPS                ("mup"), 
    NEW_AGE_NUANCE          ("na"), 
    SPIRIT_SONG             ("nat"), 
    OGS_OLD_SCHOOL          ("os"), 
    PIANO_PERFECT           ("pp"), 
    PS_I_LOVE_YOU           ("ps"), 
    HIP_HOP_STOP            ("rap"),
    REGGAE                  ("reg"), 
    ROCKIN_COUNTRY          ("rc"), 
    RNB_CLASSICS            ("rnb"), 
    ROCK                    ("rr"), 
    JAZZ_SO_SMOOTH          ("sj"),
    SOULFUL_SEASONS         ("ssx"), 
    THE_BIG_SCORE           ("st"),
    TEXAS_RED_DIRT          ("tx"), 
    URBAN_LOUNGE            ("uac"), 
    URBAN_JAMS              ("ur"), 
    WORLD                   ("wd"), 
    LADYLAND                ("ws"),
    CHRISTMAS_CELEBRATION   ("xmas");

    private final String abbreviation;
        
    private GENRES(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation(){
        return abbreviation;
    }
    
    public static boolean contains(String abb){
        boolean found = false;
        for(GENRES genre : GENRES.values()){
            if(genre.getAbbreviation().equals(abb)){
                found = true;
                break;
            }
        }
        return found;
    }
}
