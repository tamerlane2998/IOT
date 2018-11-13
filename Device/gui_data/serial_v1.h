/********************************************************/
/*               asdcxsd v1.0 13/11/2018                 */
/********************************************************/

class Serial_make
{
  private:
    int pin_input; // cong
    int pin_frequency;
    int frequency;
    String S;
    int sl_bit;
    int Bit[13];
  public:
    void open_send(int pout, int pfre, int fre);
    void open_receive(int pin, int pfre);
    void receive_byte();
    void  write(String s);
    void send_byte(int x);
    int available();
    char read();
    int get_fre();
    void start_key();
    void change();
};
void Serial_make::send_byte(int x)
{
  int a[12];
  for(int i= 1; i<= 10; i++) a[i] = 0;
  a[0] = a[9] = 1;
  for(int i =1; x > 0; i++){
    a[i] = x%2;
    x /=2;
  }
  for(int i= 1; i < 9; i++)
  {
    int status_pin_input = LOW;
    if (a[i]) status_pin_input = HIGH;

    if (i != 1) digitalWrite(pin_input, LOW); // key start
    else digitalWrite(pin_input, HIGH); // key start

    digitalWrite(pin_frequency, HIGH);  // tang dien ap


    delay(frequency);

    digitalWrite(pin_input, status_pin_input);
    digitalWrite(pin_frequency, LOW);  // giam dien ap
    delay(frequency);

  }
}
void Serial_make::open_send(int pout, int pfre, int fre)
{
  this->pin_input = pout;
  this->pin_frequency = pfre;
  this->frequency = fre;
  pinMode(pin_input, OUTPUT);
  pinMode(pin_frequency, OUTPUT);
  digitalWrite(pin_frequency, LOW);
}

void  Serial_make::write(String s)
{

  for(int i= 0; i<s.length(); i++)
    {
      send_byte((int)s[i]);
    }
}

void Serial_make::open_receive(int pin, int pfre)
{
  this->pin_input = pin;
  this->pin_frequency = pfre;

  pinMode(pin_input, INPUT);
  pinMode(pin_frequency, INPUT);

  sl_bit = 0;
  S = "";

}
int  Serial_make::available()
{
  return S.length();
}
char Serial_make::read()
{
  if (available() == 0) return char(0);
  char ch = S[0];
  S.remove(0);
  return ch;


}
void Serial_make::receive_byte()
{
  int status = digitalRead(pin_input);

  if (status == HIGH){
      Bit[sl_bit++] = 1;
  }else Bit[sl_bit++] = 0;
  if (sl_bit == 8){
    sl_bit = 0;
    int ch = 0;
    for(int i= 0; i< 8; i++) ch += Bit[i]*(1<<i);
    S += char(ch);
  }
}

void Serial_make::start_key()
{
    int status = digitalRead(pin_input);
    if (status == HIGH)
    {
      sl_bit = 0;
    }
}
int Serial_make::get_fre()
{
  return digitalPinToInterrupt(pin_frequency);
}
void Serial_make::change()
{
  if (digitalRead(pin_frequency) == HIGH) start_key();
  else receive_byte();
}
