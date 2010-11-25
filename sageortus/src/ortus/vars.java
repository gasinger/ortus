/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus;

/**
 *
 * @author jphipps
 */
public class vars {

        public static enum CacheType {
            Media,
            Series,
            Episode
        }
        
	public static enum LogLevel {
                Trace2,
		Trace,
		Debug,
		Info,
		Warning,
		Error,
		Fatal,
		Off
	}

        public static enum OrtusMediaType {
                MediaFile,
                Episode,
                Series
        }
        
    public static enum MediaType {

        None {

            @Override
            int getMediaType() {
                return 0;
            }
        },
        Recording {

            @Override
            int getMediaType() {
                return 1;
            }
        },
        Movie {

            @Override
            int getMediaType() {
                return 2;
            }
        },
        Series {

            @Override
            int getMediaType() {
                return 3;
            }
        },
        Home {

            @Override
            int getMediaType() {
                return 4;
            }
        },
        Picture {

            @Override
            int getMediaType() {
                return 5;
            }
        },
        Music {

            @Override
            int getMediaType() {
                return 6;
            }
        };

        abstract int getMediaType();
    }

	public static enum MediaGroup {
		Recorded,
		Imported
	}

	public static enum ScanType {
		None,
		FanartScan,
		MissingFanart,
		RecordingScan,
		FullScan
	};

	public static enum EvenType {
		Broadcast,
		Server,
		Clients,
		Local
	}

	public enum DownloadStatus {
		WaitingToStart,
		Running,
		Completed,
		Failed,
		UrlNotFound,
		Retrieved,
		Scheduled
	}

        public enum UserAgent {
                Firefox,
                Quicktime
        }
}
