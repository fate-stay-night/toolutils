package xyz.vimtool.media;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import ws.schild.jave.DefaultFFMPEGLocator;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;
import xyz.vimtool.media.domain.ImageMetaInfo;
import xyz.vimtool.media.domain.VideoMetaInfo;
import xyz.vimtool.media.domain.gif.AnimatedGifEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于FFmpeg内核来编解码音视频信息；
 * 使用jave-1.0.2.jar自带的FFmpeg.exe程序来处理音视频，这样无需在本地安装部署FFmpeg应用程序，且自适应linux和windows等系统；
 * 可直接在本程序中执行FFmpeg命令，实现对音视频的各种处理；
 *
 * Author: dreamer-1
 *
 */
public class MediaUtil {

	private final static String[] VIDEO_TYPE = { "ASX", "ASF", "MPG", "WMV", "3GP", "MP4", "MOV", "AVI", "FLV" }; // 可以处理的视频格式

	private final static String[] IMAGE_TYPE = { "JPG", "JPEG", "PNG", "GIF" }; // 可以处理的图片格式

    private final static String[] AUDIO_TYPE = { "MP3" }; // 可以处理的音频格式

	private static final Time DEFAULT_TIME = new Time(0, 0, 10); // 视频帧抽取时的默认时间点

	private static String FFMPEG_PATH;

	private static int DEFAULT_WIDTH = 320; // 抽取的视频帧的默认宽度值（单位：px）

    private static int DEFAULT_GIF_PLAYTIME = 110; // 抽取多张视频帧以合成gif动图时，gif的播放速度

    /**
     * 初始化时利用反射获取jave-1.0.1.jar中FFmpeg.exe的路径
     * 利用jave-1.0.1.jar来避免本地安装FFmpeg.exe
     */
	static {
		DefaultFFMPEGLocator locator = new DefaultFFMPEGLocator();
		try {
			Method method = locator.getClass().getDeclaredMethod("getFFMPEGExecutablePath");
			method.setAccessible(true);
			FFMPEG_PATH = (String) method.invoke(locator);
			method.setAccessible(false);
			System.out.println("--- 获取FFmpeg可执行路径成功 --- 路径信息为：" + FFMPEG_PATH);
		} catch (Exception e) {
			System.out.println("--- 获取FFmpeg可执行路径失败！ --- 错误信息： " + e.getMessage());
		}
	}

	/**
	 * 获取FFmpeg程序的路径（windows和linux环境下路径不一样）
	 *
	 * @return
	 */
	public static String getFFmpegPath() {
		return FFMPEG_PATH;
	}

	/**
	 *
	 * 
	 * @param commonds
	 */

    /**
     * 执行FFmpeg命令
     * @param commonds FFmpeg命令
     * @return FFmpeg执行命令过程中产生的各信息，执行出错时返回null
     */
	public static String executeCommand(List<String> commonds) {
		if (CollectionUtils.isEmpty(commonds)) {
			System.out.println("--- 指令执行失败，因为要执行的FFmpeg指令为空！ ---");
		}
        LinkedList<String> ffmpegCmds = new LinkedList<>(commonds);
		ffmpegCmds.addFirst(FFMPEG_PATH); // 设置ffmpeg程序所在路径

		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(ffmpegCmds);
			builder.start();
//			String result = waitForExcute(ffmpeg);
			System.out.println(ffmpegCmds);

			// 输出执行的命令信息
//            String cmdStr = Arrays.toString(ffmpegCmds.toArray()).replace(",", "");
//            String resultStr = result != null ? "正常" : "【异常】";
//			System.out.println("--- FFmepg命令： ---" + cmdStr + " 已执行完毕,执行结果：" + resultStr);
			return "result";

		} catch (IOException e) {
			System.out.println("--- FFmpeg命令执行出错！ --- 出错信息： " + e.getMessage());
			return null;

		} finally {
//		    if (null != ffmpeg) {
//                ProcessKiller ffmpegKiller = new ProcessKiller(ffmpeg);
//                // JVM退出时，先通过钩子关闭FFmepg进程
//                runtime.addShutdownHook(ffmpegKiller);
//            }
        }
    }

	/**
	 * FFmpeg进程执行输出，必须使用此函数，否则会出现进程阻塞现象
	 * 当FFmpeg进程执行完所有命令后，本函数返回FFmpeg进程退出时的状态值；
	 * @param process
	 * @return 进程执行命令过程中产生的各种信息，执行命令过程出错时返回null
	 */
	private static String waitForExcute(Process process) {
        InputStream inputStream = null;
        InputStream errorStream = null;
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		StringBuffer returnStr = new StringBuffer(); // 存储FFmpeg执行命令过程中产生的信息
        int exitValue = -1;
        try {
            inputStream = process.getInputStream();
            errorStream = process.getErrorStream();
			br1 = new BufferedReader(new InputStreamReader(inputStream));
			br2 = new BufferedReader(new InputStreamReader(errorStream));
            boolean finished = false;
            while (!finished) {
                try { // while内部使用一个try-catch块，这样当某一次循环读取抛出异常时，可以结束当次读取，返回条件处开始下一次读取
					String line1 = null;
					String line2 = null;
                    while ((line1 = br1.readLine()) != null) {
						System.out.println(line1);
                    }
                    while ((line2 = br2.readLine()) !=  null) {
						System.out.println(line2);
						returnStr.append(line2 + "\n");
                    }
                    exitValue = process.exitValue();
                    finished = true;
                } catch (IllegalThreadStateException e) { // 防止线程的start方法被重复调用
					System.out.println("--- 本次读取标准输出流或错误流信息出错 --- 错误信息： " + e.getMessage());
                    Thread.sleep(500);
                } catch (Exception e2) {
					System.out.println("--- 本次读取标准输出流或错误流信息出错 --- 错误信息： " + e2.getMessage());
                }
            }
            return returnStr.toString();
        } catch (Exception e) {
			System.out.println("--- 执行FFmpeg程序时读取标准输出或错误流的信息出错 ---");
            return null;
        } finally {
            try {
				if (null != br1) {
					br1.close();
				}
				if (null != br2) {
					br2.close();
				}
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != errorStream) {
					errorStream.close();
				}
			} catch (IOException e) {
				System.out.println("--- 关闭读取的标准输出流或错误流时出错 ---");
			}

        }

	}

	/**
	 * 视频转换
     * 注意：此方法会丢掉音频信息
	 *
	 * @param fileInput 源视频路径
	 * @param fileOutPut 转换后的视频路径
	 */
	private static void convertVideo(File fileInput, File fileOutPut) {
		if (null == fileInput || !fileInput.exists()) {
			throw new RuntimeException("源视频文件不存在，请检查源视频路径");
		}
		if (null == fileOutPut) {
		    throw new RuntimeException("转换后的视频路径为空，请检查转换后的视频存放路径是否正确");
        }

        if (!fileOutPut.exists()) {
            try {
                fileOutPut.createNewFile();
            } catch (IOException e) {
                System.out.println("视频转换时新建输出文件失败");
            }
        }
        String format = getFormat(fileInput);
        if (!isLegalFormat(format, VIDEO_TYPE)) {
            throw new RuntimeException("无法解析的视频格式：" + format);
        }
        List<String> commond = new ArrayList<String>();
        commond.add("-i");
        commond.add(fileInput.getAbsolutePath());
        commond.add("-an");  // 去掉音频

        //调整视频品质命令
        commond.add("-c:v");
        commond.add("libx264");
        commond.add("-preset");
        commond.add("veryslow");
        commond.add("-crf");
        commond.add("23"); // todo 视频质量系数
        commond.add("-y");
        commond.add(fileOutPut.getAbsolutePath());

        executeCommand(commond);
	}

	/**
	 * 视频压缩（会丢失音频信息）
	 * 
	 * @param fileInput 源视频路径
	 * @param fileOutPut 转换后的视频路径
	 */
	public static void zipVideo(File fileInput, File fileOutPut) {
		convertVideo(fileInput, fileOutPut);
	}

	/**
	 * 视频帧抽取
     * 默认抽取第10秒的帧画面，抽取的帧图片默认宽度为300px
	 * 
	 * @param videoFile 源视频路径
	 * @param fileOutPut 转换后的视频路径
	 */
	public static void cutVideoFrame(File videoFile, File fileOutPut) {
		cutVideoFrame(videoFile, fileOutPut, DEFAULT_TIME);
	}

	/**
	 * 视频帧抽取（抽取指定时间点的帧画面）
     * 抽取的视频帧图片大小默认为300px
	 * 
	 * @param videoFile 源视频路径
	 * @param fileOutPut 转换后的视频路径
	 * @param time 指定抽取视频帧的时间点
	 */
	public static void cutVideoFrame(File videoFile, File fileOutPut, Time time) {
		cutVideoFrame(videoFile, fileOutPut, time, DEFAULT_WIDTH);
	}

	/**
	 * 视频帧抽取（抽取指定时间点、指定宽度值的帧画面）
     * 只需指定视频帧的宽度，高度随宽度自动计算
	 * 
	 * @param videoFile 源视频路径
	 * @param fileOutPut 转换后的视频路径
	 * @param time 指定要抽取第几秒的视频帧（单位：s）
	 * @param width 抽取的视频帧图片的宽度（单位：px）
	 */
	public static void cutVideoFrame(File videoFile, File fileOutPut, Time time, int width) {
	    if (null == videoFile || !videoFile.exists()) {
	        throw new RuntimeException("源视频文件不存在，请检查源视频路径");
        }
        if (null == fileOutPut) {
            throw new RuntimeException("转换后的视频路径为空，请检查转换后的视频存放路径是否正确");
        }
		VideoMetaInfo info = getVideoMetaInfo(videoFile);
		if (null == info) {
			System.out.println("--- 未获取到源视频信息，视频帧抽取操作失败 --- 源视频： " + videoFile);
			return;
		}
		int height = width * info.getHeight() / info.getWidth(); // 根据宽度计算适合的高度，防止画面变形
		cutVideoFrame(videoFile, fileOutPut, time, width, height);
	}

	/**
	 * 视频帧抽取（抽取指定时间点、指定宽度值、指定高度值的帧画面）
	 * 
	 * @param videoFile 源视频路径
	 * @param fileOutPut 转换后的视频路径
	 * @param time 指定要抽取第几秒的视频帧（单位：s）
	 * @param width 抽取的视频帧图片的宽度（单位：px）
	 * @param height 抽取的视频帧图片的高度（单位：px）
	 */
	public static void cutVideoFrame(File videoFile, File fileOutPut, Time time, int width, int height) {
		String format = getFormat(fileOutPut);
		if (!isLegalFormat(format, IMAGE_TYPE)) {
			throw new RuntimeException("无法生成指定格式的帧图片：" + format);
		}
		String fileOutPutPath = fileOutPut.getAbsolutePath();
		if (!"GIF".equals(StringUtils.upperCase(format))) {
		    // 抽取并生成一张静态图
			cutVideoFrame(videoFile, fileOutPutPath, time, width, height, 1, false);
		} else {
		    // 抽取并生成一个gif（gif由10张静态图构成）
			String path = fileOutPut.getParent();
			String name = fileOutPut.getName();
			// 创建临时文件存储多张静态图用于生成gif
			String tempPath = path + File.separator + System.currentTimeMillis() + "_" + name.substring(0, name.indexOf("."));
			File file = new File(tempPath);
			if (!file.exists()) {
				file.mkdir();
			}
			try {
				cutVideoFrame(videoFile, tempPath, time, width, height, 10, true);
				// 生成gif
				String images[] = file.list();
				for (int i = 0; i < images.length; i++) {
					images[i] = tempPath + File.separator + images[i];
				}
				createGifImage(images, fileOutPut.getAbsolutePath(), DEFAULT_GIF_PLAYTIME);
			} catch (Exception exception) {
				System.out.println("截取视频帧操作出错");
			} finally {
			    // 删除用于生成gif的临时文件
				String images[] = file.list();
				for (int i = 0; i < images.length; i++) {
					File fileDelete = new File(tempPath + File.separator + images[i]);
					fileDelete.delete();
				}
				file.delete();
			}
		}
	}

    /**
     * 视频帧抽取（抽取指定时间点、指定宽度值、指定高度值、指定时长、指定静态/动态属性的帧画面）
     *
     * @param videoFile 源视频
     * @param path 生成gif预览的路径
     * @param time 开始截取视频帧的时间点（单位：s）
     * @param width 截取的视频帧图片的宽
     * @param height 截取的视频帧图片的高
     * @param timeLength 截取的视频帧的时长（从time开始，单位:s，需要小于20）
     * @param isContinuty false - 静态图（只截取time时间点的那一帧图片），true - 动态图（截取从time时间点开始,timelength这段时间的多张帧然后制作成gif）
     */
    private static void cutVideoFrame(File videoFile, String path, Time time, int width, int height, int timeLength, boolean isContinuty) {
        if (videoFile == null || !videoFile.exists()) {
            throw new RuntimeException("源视频文件不存在，源视频路径： " + videoFile.getAbsolutePath());
        }
        if (null == path) {
            throw new RuntimeException("转换后的视频帧路径为空，请检查转换后的视频帧存放路径是否正确");
        }
        VideoMetaInfo info = getVideoMetaInfo(videoFile);
        if (info != null && time.getTime() + timeLength > info.getDuration()) {
            throw new RuntimeException("开始截取视频帧的时间点不合法：" + time.toString() + "，因为截取时间点晚于视频的最后时间点");
        }
        if (width <= 20 || height <= 20) {
            throw new RuntimeException("截取的视频帧图片的宽度或高度不合法，宽高值必须大于20");
        }
        try {
            List<String> commond = new ArrayList<String>();
            commond.add("-ss");
            commond.add(time.toString());
            if (isContinuty) {
                commond.add("-t");
                commond.add(timeLength + "");
            } else {
                commond.add("-vframes");
                commond.add("1");
            }
            commond.add("-i");
            commond.add(videoFile.getAbsolutePath());
            commond.add("-an");
            commond.add("-f");
            commond.add("image2");
            if (isContinuty) {
                commond.add("-r");
                commond.add("3");
            }
            commond.add("-s");
            commond.add(width + "*" + height);
            if (isContinuty) {
                commond.add(path + File.separator + "foo-%03d.jpeg");
            } else {
                commond.add(path);
            }

            executeCommand(commond);
        } catch (Exception e) {
            System.out.println("视频帧抽取过程出错");
        }
    }

    /**
     * 从解析的视频信息中提取比特率（码率）信息
     *
     * 解析出的视频信息一般为以下格式：
     * Input #0, mov,mp4,m4a,3gp,3g2,mj2, from 'E:\videos\china.mp4':
     * Duration: 00:02:25.1, start: 0.000000, bitrate: 452 kb/s
     * Stream #0.0(und): Video: h264, yuv420p, 640x360 [PAR 0:1 DAR 0:1], 25.00 tb(r)
     * Stream #0.1(und): Audio: mpeg4aac, 44100 Hz, stereo
     *
     * 注解：
     * Duration: 00:02:25.1（视频时长）, start: 0.000000（视频开始时间）, bitrate: 452 kb/s（视频比特率/码率）
     * Stream #0.0(und): Video: h264(视频编码格式), yuv420p（视频数据格式）, 640x360（视频分辨率） [PAR 0:1 DAR 0:1], 25.00（视频帧率） tb(r)
     *
     * @param videoInfo 解析出的视频信息
     * @return 源视频的比特率，单位kb/s；当传入的解析后的视频信息为空时，返回-1
     */
    public static Integer getVideoBitrate(String videoInfo) {
        if (null == videoInfo) {
            System.out.println("--- 解析出的视频信息为空，无法从中获取比特率 ---");
            return -1;
        }
        String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s"; // (.*?)表示：匹配任何除\r\n之外的任何0或多个字符，非贪婪模式
        Pattern pattern = Pattern.compile(regexDuration);
        Matcher macher = pattern.matcher(videoInfo);
        String duration = "";
        String startTime = "";
        String bitrate = "";
        Integer bitrateNum = -1;
        if (macher.find()) {
            duration = macher.group(1) + "ms";
            startTime = macher.group(2) + "ms";
            bitrate = macher.group(3) + "kb/s";
            bitrateNum = Integer.parseInt(macher.group(3));
        }
        return bitrateNum;
    }

    /**
     * 从解析的视频信息中提取帧率信息
     * @param videoInfo 解析出的视频信息
     * @return 源视频的帧率，单位fps；当传入的解析后的视频信息为空时，返回-1F
     */
    public static Float getVideoFramerate(String videoInfo) {
        if (null == videoInfo) {
            System.out.println("--- 解析出的视频信息为空，无法从中获取帧率 ---");
            return -1F;
        }
        String regexStream = "Stream #0\\.0\\(und\\): Video: (.*?), (.*?), (.*?)x(.*?) \\[PAR (.*?) DAR (.*?)\\], (.*?) tb\\(r\\)";
        Pattern pattern = Pattern.compile(regexStream);
        Matcher macher = pattern.matcher(videoInfo);
        String decoder = "";
        String fomate = "";
        String height = "";
        String width = "";
        String framerate = "";
        Float framerateFloat = -1F;
        if (macher.find()) {
            decoder = macher.group(1);
            fomate = macher.group(2);
            height = macher.group(3) + "px";
            width = macher.group(4) + "px";
            framerate = macher.group(7) + "fps";
            framerateFloat = Float.parseFloat(macher.group(7));
        }
        return framerateFloat;
    }

// todo 抽取的音频信息无法播放
    /**
     * 抽取视频里的音频信息
     * 只能抽取成MP3文件
     * @param videoFile 源视频文件
     * @param audioFile 从源视频提取的音频文件
     */
	public static void getAudioFromVideo(File videoFile, File audioFile) {
		if (null == videoFile || !videoFile.exists()) {
			throw new RuntimeException("源视频文件不存在： " + videoFile.getAbsolutePath());
		}
		if (null == audioFile) {
			throw new RuntimeException("要提取的音频路径为空：" + audioFile.getAbsolutePath());
		}
        String format = getFormat(audioFile);
        if (!isLegalFormat(format, AUDIO_TYPE)) {
            throw new RuntimeException("无法生成指定格式的音频：" + format);
        }
		try {
			if (!audioFile.exists()) {
				audioFile.createNewFile();
			}

			List<String> commond = new ArrayList<>();
			commond.add("-i");
			commond.add(videoFile.getAbsolutePath());
			commond.add("-vn");
			commond.add("-y");
			commond.add("-acodec");
			commond.add("copy");
			commond.add(audioFile.getAbsolutePath());
			executeCommand(commond);
		} catch (Exception e) {
			System.out.println("--- 抽取视频中的音频信息的过程出错 ---");
		}
	}

	public static void main(String[] args) {
		String file = "/Users/xiao/Desktop/58.mp4";
		String outFile = "/Users/xiao/Desktop/67.mp4";
		List<String> cmd = new ArrayList<>();
		cmd.add(FFMPEG_PATH);
		cmd.add("-ss");
		cmd.add("0");
		cmd.add("-t");
		cmd.add("6");
		cmd.add("-i");
		cmd.add(file);
		cmd.add("-vcodec");
		cmd.add("copy");
		cmd.add("-acodec");
		cmd.add("copy");
		cmd.add("-aspect");
		cmd.add("4:3");
		cmd.add("-y");
		cmd.add(outFile);
		System.out.println(cmd);
		try {
			//调用线程命令启动转码
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(cmd);
			builder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("$$$$$$$$$$$finish");
	}

	// todo 截取的视频无法播放
	/**
	 * 截取视频中的某一段，生成新视频
	 * 
	 * @param videoFile 源视频路径
	 * @param outputFile 转换后的视频路径
	 * @param startTime 开始抽取的时间点，单位:s
	 * @param timeLength 需要抽取的时间段，单位:s；例如：该参数值为10时即抽取从startTime开始之后10秒的视频作为新视频
	 */
	public static void cutVideo(File videoFile, File outputFile, Time startTime, int timeLength) {
		if (videoFile == null || !videoFile.exists()) {
			throw new RuntimeException("视频文件不存在：" + videoFile.getAbsolutePath());
		}
        if (null == outputFile) {
            throw new RuntimeException("转换后的视频路径为空，请检查转换后的视频存放路径是否正确");
        }
		VideoMetaInfo info = getVideoMetaInfo(videoFile);
		if (info != null && startTime.getTime() + timeLength > info.getDuration()) {
			throw new RuntimeException("截取时间不合法：" + startTime.toString() + "，因为截取时间大于视频的时长");
		}
		try {
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			List<String> commond = new ArrayList<>();
			commond.add("-ss");
			commond.add(startTime.toString());
			commond.add("-t");
			commond.add("" + timeLength);
			commond.add("-i");
			commond.add(videoFile.getAbsolutePath());
			commond.add("-vcodec");
			commond.add("copy");
			commond.add("-acodec");
			commond.add("copy");
			commond.add(outputFile.getAbsolutePath());
			executeCommand(commond);
		} catch (IOException e) {
			System.out.println("--- 视频截取过程出错 ---");
		}
	}

    /**
     * 获取视频的基本信息（从文件中）
     *
     * @param videoFile 源视频路径
     * @return 视频的基本信息，解码失败时返回null
     */
    public static VideoMetaInfo getVideoMetaInfo(File videoFile) {
        VideoMetaInfo videoInfo = new VideoMetaInfo();
        try {
            if (null == videoFile || !videoFile.exists()) {
                return null;
            }
			MultimediaObject instance = new MultimediaObject(videoFile);
			MultimediaInfo info = instance.getInfo();
            videoInfo.setWidth(info.getVideo().getSize().getWidth()); // 获取视频宽度
            videoInfo.setHeight(info.getVideo().getSize().getHeight()); // 获取视频高度
            videoInfo.setDuration(info.getDuration()); // 获取视频时长，单位为ms
            videoInfo.setSize(videoFile.length());  // 获取视频大小（体积大小，单位为b）
            videoInfo.setFormat(info.getFormat()); // 获取视频格式名称
            videoInfo.setDecoder(info.getVideo().getDecoder()); // 获取视频解码器

            int bitRate = info.getVideo().getBitRate(); // 获取视频（平均）比特率，即平均码率，值小于0时表示无效值
            if (bitRate < 0) { // jave无法获取有效视频比特率时从FFmpeg中手工截取
                bitRate = getVideoBitrate(getVideoInfoFromFFmpeg(videoFile));
            }
            videoInfo.setBitRate(bitRate);

            float framerate = info.getVideo().getFrameRate(); // 获取视频帧率，值小于0时表示无效值
            //if (framerate < 0) {
                framerate = getVideoFramerate(getVideoInfoFromFFmpeg(videoFile));
            //}
            videoInfo.setFrameRate(framerate);
            return videoInfo;
        } catch (Exception e) {
            System.out.println("--- 解码视频信息失败 ---");
            return null;
        }
    }

    /**
     * 获取视频的基本信息（从流中）
     *
     * @param inputStream 源视频路径
     * @return 视频的基本信息，解码失败时返回null
     */
    public static VideoMetaInfo getVideoMetaInfo(InputStream inputStream) {
        Encoder encoder = new Encoder();
        VideoMetaInfo videoInfo = new VideoMetaInfo();
        try {
            File file = File.createTempFile("tmp", null);
            if (!file.exists()) {
                return null;
            }
            FileUtils.copyInputStreamToFile(inputStream, file);
            videoInfo = getVideoMetaInfo(file);
            file.deleteOnExit();
            return videoInfo;
        } catch (Exception e) {
            System.out.println("--- 从流中获取视频基本信息出错 ---");
            return null;
        }
    }

    /**
     * 使用FFmpeg的"-i"命令来解析视频信息
     * @param videoFile 源视频
     * @return 解析后的结果字符串，解析失败时为空
     */
    private static String getVideoInfoFromFFmpeg(File videoFile) {
        if (videoFile == null || !videoFile.exists()) {
            throw new RuntimeException("源视频文件不存在，源视频路径： " + videoFile.getAbsolutePath());
        }
        List<String> commond = new ArrayList<String>();
        commond.add("-i");
        commond.add(videoFile.getAbsolutePath());
        String executeResult = MediaUtil.executeCommand(commond);
        return executeResult;
    }

    /**
     * 检测视频格式是否合法
     * @param format
     * @param formats
     * @return
     */
	private static boolean isLegalFormat(String format, String formats[]) {
		for (String item : formats) {
			if (item.equals(StringUtils.upperCase(format))) {
				return true;
			}
		}
		return false;
	}

    /**
     * 创建gif
     *
     * @param image 多个jpg文件名（包含路径）
     * @param outputPath 生成的gif文件名（包含路径）
     * @param playTime 播放的延迟时间，可调整gif的播放速度
     */
	private static void createGifImage(String image[], String outputPath, int playTime) {
        if (null == outputPath) {
            throw new RuntimeException("转换后的GIF路径为空，请检查转换后的GIF存放路径是否正确");
        }
		try {
			AnimatedGifEncoder encoder = new AnimatedGifEncoder();
			encoder.setRepeat(0);
			encoder.start(outputPath);
			BufferedImage src[] = new BufferedImage[image.length];
			for (int i = 0; i < src.length; i++) {
				encoder.setDelay(playTime); // 设置播放的延迟时间
				src[i] = ImageIO.read(new File(image[i])); // 读入需要播放的jpg文件
				encoder.addFrame(src[i]); // 添加到帧中
			}
			encoder.finish();
		} catch (Exception e) {
			System.out.println("--- 多张静态图转换成动态GIF图的过程出错 ---");
		}
	}


	/**
	 * 获取图片的基本信息（从流中）
	 * 
	 * @param inputStream 源图片路径
	 * @return 图片的基本信息，获取信息失败时返回null
	 */
	public static ImageMetaInfo getImageInfo(InputStream inputStream) {
		BufferedImage image = null;
		ImageMetaInfo imageInfo = new ImageMetaInfo();
		try {
			image = ImageIO.read(inputStream);
			imageInfo.setWidth(image.getWidth());
			imageInfo.setHeight(image.getHeight());
			imageInfo.setSize(Long.valueOf(String.valueOf(inputStream.available())));
            return imageInfo;
		} catch (Exception e) {
            System.out.println("--- 获取图片的基本信息失败 ---");
            return null;
		}
	}

	/**
	 * 获取图片的基本信息 （从文件中）
	 * 
	 * @param imageFile 源图片路径
	 * @return 图片的基本信息，获取信息失败时返回null
	 */
	public static ImageMetaInfo getImageInfo(File imageFile) {
		BufferedImage image = null;
		ImageMetaInfo imageInfo = new ImageMetaInfo();
		try {
			if (null == imageFile || !imageFile.exists()) {
				return null;
			}
			image = ImageIO.read(imageFile);
			imageInfo.setWidth(image.getWidth());
			imageInfo.setHeight(image.getHeight());
			imageInfo.setSize(imageFile.length());
			imageInfo.setFormat(getFormat(imageFile));
            return imageInfo;
		} catch (Exception e) {
			System.out.println("--- 获取图片的基本信息失败 ---");
			return null;
		}
	}

    /**
     * 获取指定文件的后缀名
     * @param file
     * @return
     */
	private static String getFormat(File file) {
		String fileName = file.getName();
		String format = fileName.substring(fileName.indexOf(".") + 1);
		return format;
	}


    /**
     * 在程序退出前结束已有的FFmpeg进程
     */
	private static class ProcessKiller extends Thread {
		private Process process;

		public ProcessKiller(Process process) {
			this.process = process;
		}

		@Override
        public void run() {
			this.process.destroy();
            System.out.println("--- 已销毁FFmpeg进程 --- 进程名： " + process.toString());
		}
	}

}