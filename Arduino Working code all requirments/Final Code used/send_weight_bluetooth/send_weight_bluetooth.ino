#include <Q2HX711.h>
#include <SoftwareSerial.h>
#include <EEPROM.h>

SoftwareSerial ble_device(10,11); // CC2541 TX/RX pins

const byte hx711_data_pin = 4;
const byte hx711_clock_pin = 5;
float y1; 
long x1 ;
long x0;
float avg_size = 15.0; // amount of averages for each mass measurement
char ble_reading = 'Z'; // Bluetooth char preallocation
float ratio_2 = 0.0; // for linear fit

int addr1=0;

Q2HX711 hx711(hx711_data_pin, hx711_clock_pin); // prep hx711

void setup()
{
  delay(1000);
  ble_device.begin(57600);
  Serial.begin(57600);
  EEPROM.get(addr1,y1);
  addr1+=sizeof(float);
  EEPROM.get(addr1,x0);
  addr1+=sizeof(float);
  EEPROM.get(addr1,x1);
  Serial.print("X0 value is ");
  Serial.println(x0);
  Serial.print("X1 value is ");
  Serial.println(x1);
  Serial.print("Y1 value is ");
  Serial.println(y1);
    
}

void loop()
{
    long reading = 0L;
    for (int jj=0;jj<int(avg_size);jj++){
      reading+=hx711.read();
    }
    reading/=long(avg_size);
    float ratio_1 =(float) (reading-x0);                                                                               
    float ratio_2 =(float) (x1-x0);
    float ratio = ratio_1/ratio_2;
    float weight = y1*ratio;
    char ble_str[9];

    for (int i=0;i<7;i++){
      ble_str[i] = '0';
    }

    dtostrf(weight,7,1,ble_str);
    Serial.print("Weight is:");
    Serial.println(ble_str);
    ble_str[7] = '\n';
    ble_str[8]='\0';    
    ble_device.write(ble_str);
    delay(1000);
}
