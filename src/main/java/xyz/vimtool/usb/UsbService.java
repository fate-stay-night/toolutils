package xyz.vimtool.usb;

import javax.usb.*;
import javax.usb.util.UsbUtil;
import java.util.List;

/**
 * USB数据监听
 *
 * @author zhangzheng
 * @version 1.0.0
 * @date 2020/4/19
 */
public class UsbService {

    private static final short VENDOR_ID = 0x046d;

    /**
     * The product ID of the missile launcher.
     */
    private static final short PRODUCT_ID = (short) 0xc084;


    private static final byte INTERFACE_AD = 0x00;
    private static final byte ENDPOINT_OUT = 0x02;
    private static final byte ENDPOINT_IN = (byte) 0x86;
    private static final byte[] COMMAND = {0x01, 0x00};

    @SuppressWarnings("unchecked")
    public static UsbDevice findMissileLauncher(UsbHub hub) {
        UsbDevice launcher = null;

        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            if (device.isUsbHub()) {
                launcher = findMissileLauncher((UsbHub) device);
                if (launcher != null)
                    return launcher;
            } else {
                UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
                if (desc.idVendor() == VENDOR_ID
                        && desc.idProduct() == PRODUCT_ID) {
                    System.out.println("发现设备" + device);
                    return device;
                }
            }
        }
        return null;
    }

    //command for controlTransfer
    public static void sendMessage(UsbDevice device, byte[] message)
            throws UsbException {
        UsbControlIrp irp = device
                .createUsbControlIrp(
                        (byte) (UsbConst.REQUESTTYPE_TYPE_CLASS | UsbConst.REQUESTTYPE_RECIPIENT_INTERFACE),
                        (byte) 0x09, (short) 2, (short) 1);
        irp.setData(message);
        device.syncSubmit(irp);
    }

    /**
     * Class to listen in a dedicated Thread for data coming events.
     * This really could be used for any HID device.
     */
    public static class HidMouseRunnable implements Runnable {
        /* This pipe must be the HID interface's interrupt-type in-direction endpoint's pipe. */
        public HidMouseRunnable(UsbPipe pipe) {
            usbPipe = pipe;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[UsbUtil.unsignedInt(usbPipe.getUsbEndpoint().getUsbEndpointDescriptor().wMaxPacketSize())];
            @SuppressWarnings("unused")
            int length = 0;

            while (running) {
                try {
                    length = usbPipe.syncSubmit(buffer);
                } catch (UsbException uE) {
                    if (running) {
                        System.out.println("Unable to submit data buffer to HID mouse : " + uE.getMessage());
                        break;
                    }
                }
                if (running) {
                    try {
                        String result = getHexString(buffer);
                        System.out.println(result);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * Stop/abort listening for data events.
         */
        public void stop() {
            running = false;
            usbPipe.abortAllSubmissions();
        }

        public boolean running = true;
        public UsbPipe usbPipe = null;
    }

    private static String getHexString(byte[] buffer) {
        StringBuilder result = new StringBuilder();
        for (byte b : buffer) {
            result.append(" 0x").append(UsbUtil.toHexString(b));
        }
        return result.toString();
    }

    /**
     * get the  correct Interface for USB
     *
     * @param device
     * @return
     * @throws UsbException
     */
    public static UsbInterface readInit() throws UsbException {
        UsbDevice device = findMissileLauncher(UsbHostManager.getUsbServices()
                .getRootUsbHub());
        if (device == null) {
            System.out.println("Missile launcher not found.");
            System.exit(1);
        }
        UsbConfiguration configuration = device.getActiveUsbConfiguration();
        UsbInterface iface = configuration.getUsbInterface(INTERFACE_AD);//Interface Alternate Number
        //if you using the MS os,may be you need judge,because MS do not need follow code,by tong
        iface.claim(new UsbInterfacePolicy() {
            @Override
            public boolean forceClaim(UsbInterface arg0) {
                // TODO Auto-generated method stub
                return true;
            }
        });
        return iface;
    }

    /**
     * 异步bulk传输,by tong
     *
     * @param usbInterface
     * @param data
     */
    public static void syncSend(UsbInterface usbInterface, byte[] data) {
        UsbEndpoint endpoint = usbInterface.getUsbEndpoint(ENDPOINT_OUT);
        UsbPipe outPipe = endpoint.getUsbPipe();
        try {
            outPipe.open();
            outPipe.syncSubmit(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                outPipe.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static HidMouseRunnable listenData(UsbInterface usbInterface) {
//        UsbEndpoint endpoint = usbInterface.getUsbEndpoint(ENDPOINT_IN);
        UsbEndpoint endpoint = (UsbEndpoint)usbInterface.getUsbEndpoints().get(0);

        UsbPipe inPipe = endpoint.getUsbPipe();
        HidMouseRunnable hmR = null;
        try {
            inPipe.open();
            hmR = new HidMouseRunnable(inPipe);
            Thread t = new Thread(hmR);
            t.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hmR;
    }

    /**
     * 主程序入口
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(0xc084);
        System.out.println(0x046d);
        // TODO Auto-generated method stub
        UsbInterface iface;
        try {
            iface = readInit();
            listenData(iface);
            syncSend(iface, COMMAND);
        } catch (UsbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
