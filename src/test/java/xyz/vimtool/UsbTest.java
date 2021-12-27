package xyz.vimtool;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Test;

import javax.usb.*;
import java.util.ArrayList;
import java.util.List;

/**
 * usb通信测试
 *
 * @author zhangzheng
 * @version 1.0.0
 * @date 2020/4/19
 */
public class UsbTest {

    private static final short VENDOR_ID = 0x046d;

    /** The product ID of the missile launcher. */
    private static final short PRODUCT_ID = (short) 0xc084;

    @Test
    public void getUsbs() throws UsbException {
        UsbServices usbServices = UsbHostManager.getUsbServices();
        UsbHub usbHub = usbServices.getRootUsbHub();

        List<UsbDevice> usbDevices = getAllUsbDevices(usbHub);

        for (UsbDevice usbDevice : usbDevices) {
            System.out.println(ReflectionToStringBuilder.toString(usbDevice));
        }
    }

    public static List<UsbDevice> getAllUsbDevices(UsbDevice usbDevice) {
        List<UsbDevice> list = new ArrayList<>();

        list.add(usbDevice);

        /* this is just normal recursion.  Nothing special. */
        if (usbDevice.isUsbHub()) {
            List<UsbDevice> devices = ((UsbHub) usbDevice).getAttachedUsbDevices();
            for (Object device : devices) {
                list.addAll(getAllUsbDevices((UsbDevice) device));
            }
        }

        return list;
    }

    @Test
    public void Usb() throws Exception {
        System.out.println(VENDOR_ID);
        System.out.println(PRODUCT_ID);
        UsbServices usbServices = UsbHostManager.getUsbServices();
        UsbHub usbHub = usbServices.getRootUsbHub();
        UsbDevice usbDevice = findMissileLauncher(usbHub);
        System.out.println(usbDevice);
    }

    /**
     * 依据VID和PID找到设备device
     *
     * @param hub
     * @return
     */
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
                System.out.println(desc.idVendor() + " " + desc.idProduct());
                if (desc.idVendor() == VENDOR_ID
                        && desc.idProduct() == PRODUCT_ID) {
                    System.out.println("找到设备：" + device);
                    return device;
                }
            }
        }
        return null;
    }
}
