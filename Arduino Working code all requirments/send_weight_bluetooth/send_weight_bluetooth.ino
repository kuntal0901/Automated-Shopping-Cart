#include <Q2HX711.h>
#include <SoftwareSerial.h>

SoftwareSerial ble_device(10,11); // CC2541 TX/RX pins

const byte hx711_data_pin = 4;
const byte hx711_clock_pin = 5;
float y1 = 183; // calibrated mass to be added
long x1 =8380274;
long x0 = 8376204;
float avg_size = 15.0; // amount of averages for each mass measurement
char ble_reading = 'Z'; // Bluetooth char preallocation
float ratio_2 = 0.0; // for linear fit


Q2HX711 hx711(hx711_data_pin, hx711_clock_pin); // prep hx711

void setup() {
  ble_device.begin(57600); // prepare BLE module
  delay(1000); // allow load cell and hx711 to settle
  Serial.begin(57600);
  // // tare procedure
  // for (int ii=0;ii<int(avg_size);ii++){
  //   delay(10);
  //   x0+=hx711.read();
  // }
  // x0/=long(avg_size);
}

void loop() {
// if (x1==-1L or y1==-1.0){
//     // String char_str = "15.7";
//     // // // ble_device.write("Send Cal");
//     // // while(true){
//     // // //   ble_reading = ble_device.read();
      
//     // // //   if (ble_reading==-1){
//     // // //   } else {
//     // // //     if (ble_reading=='\n' or ble_reading=='\r'){
//     // // //       break;
//     // // //     } else{
//     // // //       char_str+=ble_reading;
//     // // //     }
//     // // //   }
//     // // // } 

//     // // for (int jj=0;jj<int(avg_size);jj++){
//     // //   x1+=hx711.read();
//     // // }
//     // // x1/=long(avg_size);
//   //   // y1 = char_str.toFloat();
//   // } else {
    long reading = 0L;
    for (int jj=0;jj<int(avg_size);jj++){
      reading+=hx711.read();
    }
    reading/=long(avg_size);
    float ratio_1 =(float) (reading-x0);                                                                               
    float ratio_2 =(float) (x1-x0);
    float ratio = ratio_1/ratio_2;
    float weight = y1*ratio;
    char ble_str[10];
    for (int i=0;i<7;i++){
      ble_str[i] = '0';
    }
    dtostrf(weight,7,1,ble_str);
    Serial.print("Weight is:");
    Serial.println(ble_str);
    ble_str[7] = '\r';
    ble_str[8] = '\n';
    ble_str[9] ='\0';
    
    ble_device.write(ble_str);
  // }
}
