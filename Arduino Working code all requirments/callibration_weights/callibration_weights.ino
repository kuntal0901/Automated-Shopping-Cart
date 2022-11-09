#include <Q2HX711.h>
#include <EEPROM.h>
const byte hx711_data_pin = 4;
const byte hx711_clock_pin = 5;

float y1; // calibrated mass to be added
long x1 = 0L;
long x0 = 0L;
int avg_size = 15; // amount of averages for each mass measurement

int addr1=0;

Q2HX711 hx711(hx711_data_pin, hx711_clock_pin); // prep hx711

void setup() {
  Serial.begin(57600); // prepare serial port
  delay(1000); // allow load cell and hx711 to settle
  // tare procedure
  Serial.println("Taring in progress");
  for (int ii=0;ii<int(avg_size);ii++)
  {
    delay(100);
    x0+=hx711.read();
  }
  x0/=long(avg_size);
  Serial.println("Place Callibrated Mass onto loadcell and enter its weight whenever done");
  while(Serial.available()==0){

  }
  float weight_placed=Serial.parseFloat();
  EEPROM.put(addr1,weight_placed);
  // calibration procedure (mass should be added equal to y1)
  addr1+=sizeof(float);
  int ii = 1;
  while(true)
  {
    if (hx711.read()>x0+10000){
    } else {
      ii++;
      delay(2000);
      for (int jj=0;jj<int(avg_size);jj++){
        x1+=hx711.read();
      }
      x1/=long(avg_size);
      break;
    }
  }
  y1=weight_placed;
  EEPROM.put(addr1,x0);
  addr1+=sizeof(long);
  EEPROM.put(addr1,x1);
  // addr1+=sizeof(long);
  Serial.println("Calibration Complete");
  Serial.print("X1 value is ");
  Serial.println(x1);
  Serial.print("X0 value is ");
  Serial.println(x0);
  
}

void loop() {
  // averaging reading
  // long reading = 0;
  // for (int jj=0;jj<int(avg_size);jj++){
  //   reading+=hx711.read();
  // }
  // reading/=long(avg_size);
  // // calculating mass based on calibration and linear fit
  // float ratio_1 = (float) (reading-x0);
  // float ratio_2 = (float) (x1-x0);
  // float ratio = ratio_1/ratio_2;
  // float mass = y1*ratio;
  // Serial.print("Raw: ");
  // Serial.print(reading);
  // Serial.print(", ");
  // Serial.println(mass);
  // delay(10000);
}