import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class shortest_path extends PApplet {

int x_city[];
int y_city[];
int shortest_route_x[];
int shortest_route_y[];
int shortest_distance = 0;
int temp_route_x[]; //store the temporory city order
int temp_route_y[]; //store the temporary city order
boolean distance_initialized = false;

int n_cities = 6;
int canvas_width = 600;
int canvas_height = 600;
int circle_diameter = 30;
int robot_diameter = 30;
int grid_size = circle_diameter;
float grid_thickness = 0.2f;
float robot_init_x = robot_diameter/2;
float robot_init_y = robot_diameter/2;
float robot_curr_x = 0, robot_curr_y = 0;
float robot_curr_angle = 0;
boolean debug = false;


boolean called = false;

int north = 1;
int north_east = 2;
int north_west = 3;
int east = 4;

int south = (-1)*north;
int south_east = (-1) *  north_east;
int south_west = (-1) * north_west;
int west = (-1)*east;

int count = 0;

int n_column = canvas_width / grid_size;
int n_rows = canvas_height / grid_size;

int x_coord = (int)circle_diameter/2, y_coord = (int)circle_diameter/2;
boolean forward = true;

public float degree_to_rad(float angle)
{
   float radians = (angle * 3.1415f)/180;
   return  radians;
}

public float rad_to_degree(float rad)
{
   float angle = rad * (180/3.1415f);
   return angle;
}

public void draw_random_junctions()
{
   //reference 
   //draw_circle(10,10,circle_diameter, 1);

   int x_lower_bound = (int)(canvas_width * 10/100);
   int y_lower_bound = (int)(canvas_height * 10/100);
   int x_upper_bound = (int)(canvas_width - (canvas_width * 10/100));
   int y_upper_bound = (int)(canvas_height - (canvas_height * 10/100));
   x_city[0] =   (int)random(x_lower_bound, x_upper_bound);
   y_city[0] =   (int)random(y_lower_bound, y_upper_bound);  
  
   for(int i=1; i<n_cities; i++){
      x_city[i] = ((x_city[0] +  (int) random(x_lower_bound)) % x_upper_bound); 
      y_city[i] = ((y_city[0] +  (int) random(y_lower_bound)) % y_upper_bound); 
   }
   

   for(int i=0;i<n_cities;i++){
      int x = x_city[i];
      int y = y_city[i];
      plot_nodes(x,y);
      temp_route_x[i] = x_city[i]; 
      temp_route_y[i] = y_city[i]; 

   }

  temp_route_x[n_cities] = x_city[0]; 
    temp_route_y[n_cities] = y_city[0]; 
 
}

public void draw_predefined_junctions()
{
  
    x_city[0] = 500; 
    x_city[1] = 320;
    x_city[2] = 200;
    x_city[3] = 96;
    x_city[4] = 50;
    x_city[5] = 300;
 
    y_city[0] = 25;
    y_city[1] = 45;
    y_city[2] = 105;
    y_city[3] = 320; 
    y_city[4] = 205;
    y_city[5] = 450;

    for(int i=0;i<n_cities;i++){
      int x = x_city[i];
      int y = y_city[i];
      plot_nodes(x,y);
      fill(255);
      text(str(i), x, y);
      temp_route_x[i] = x_city[i]; 
      temp_route_y[i] = y_city[i]; 
   }
       
  temp_route_x[n_cities] = x_city[0]; 
    temp_route_y[n_cities] = y_city[0]; 
 
}

public void identify_circles_location()
{
  float row = 0.0f, column = 0.0f;
  int r1 = 0, r2 = 0, c1 =0, c2=0;
  boolean circle_in_r2 = false;
  boolean circle_in_c2 = false;
  int cell_midpoint = circle_diameter / 2;
  for(int i = 0; i < n_cities; i++){
    column = (x_city[i] - 1)/cell_midpoint;
    row = (y_city[i] - 1)/cell_midpoint;
    r1 = PApplet.parseInt (row / 2);

    if(row%2 > 0.0f){
      r2 = r1 + 1;
      circle_in_r2 = true; 
    }else{
      circle_in_r2 = false; 
    }
    
    c1 = PApplet.parseInt(column/2);

    if(column%2 > 0.0f){
      c2 = c1 + 1;
      circle_in_c2 = true;
    }else{
      circle_in_c2 = false;
    }
    
    if(debug == true){
    
      if(circle_in_r2 == false){
        print("circle in row [ "+r1+" ]");
      }else{
        print("circle in row [ "+r1+", "+r2+" ]");
      }
    
      if(circle_in_c2 == false){
        print(" ------ column [ "+c1+" ]\n");
      }else{
        print(" ------ column [ "+c1+", "+c2+" ]\n");
      }
    }
  } 
  
}

public void inc_angle(float degree)
{
  robot_curr_angle = (robot_curr_angle + degree) % 360.0f;
}

public void dec_angle(float degree)
{
  robot_curr_angle = (robot_curr_angle - degree) % 360.0f;
}

public void draw_circle(float x, float y, int diameter, float thickness)
{
     strokeWeight(thickness);
     ellipse(x,y,diameter,diameter);
}
public void plot_nodes(int x, int y)
{
    fill(0,0,255);
    //stroke(0);
    draw_circle(x, y, circle_diameter, 0.5f);
}

public void robot()
{
    float radius = robot_diameter/2;
    fill(0,128,0);
    draw_circle(robot_curr_x, robot_curr_y, robot_diameter, 0.5f);
    stroke(255,255,0);
    strokeWeight(3);
    float x1 = robot_curr_x;
    float y1 = robot_curr_y;
    float x2 = x1 + radius * cos(degree_to_rad(robot_curr_angle));
    float y2 = y1 + radius * sin(degree_to_rad(robot_curr_angle));
    line(x1, y1, x2, y2);
}

public void move_robot_forward(float distance)
{
  float new_x = distance * cos(degree_to_rad(robot_curr_angle));
  float new_y = distance * sin(degree_to_rad(robot_curr_angle));
  robot_curr_x +=  new_x;
  robot_curr_y +=  new_y;
  robot();
}

public void move_robot_backward(int distance)
{
  float new_x = distance * cos(degree_to_rad(robot_curr_angle));
  float new_y = distance * sin(degree_to_rad(robot_curr_angle));
  robot_curr_x -=  new_x;
  robot_curr_y -=  new_y;
  robot();
}

public void turn_robot_right(float angle)
{
  inc_angle(angle);
  robot();
}

public void turn_robot_left(float angle)
{
  dec_angle(angle);
  robot();
}

public void create_grid_layout(int grid_size, int canvas_width, int canvas_height, float grid_thickness){
   stroke(0);
   strokeWeight(grid_thickness);   
   int i = 0;
   while(true){ 
     line(0, i+grid_size,canvas_width, i+grid_size);
     i = i + grid_size;
     if(i>canvas_width){
        break; 
     }

 }
 
  i = 0;
   while(true){ 
     line(i+grid_size, 0,i+grid_size, canvas_height);
     i = i + grid_size;
     if(i>canvas_height){
        break; 
     }

 } 
 identify_circles_location();
}

public void refresh_frame()
{
  background(255);
  draw_predefined_junctions();
  create_grid_layout(grid_size, canvas_width, canvas_height, grid_thickness);
}

public int get_robot_direction()
{
  int direction = 0;
  float angle = robot_curr_angle;
  if(angle == 0.0f || angle == 360.0f){
      direction = east;  
  }else if(angle == 90.0f){
      direction = south;  
  }else if(angle == 180.0f){
      direction = west;  
  }else if(angle == 270.0f){
      direction = north;  
  }else if(angle > 0.0f && angle < 90.0f){
     direction = south_east; 
  }else if(angle > 90.0f && angle < 180.0f){
     direction = south_west;
  }else if(angle > 180.0f && angle < 270.0f){
    direction = north_west;
  }else if(angle > 270.0f && angle < 360.0f){
    direction = north_east; 
  }
  
  return direction;
}

public void move_zig_zag_up_down()
{
  int grid_space = 2;
  int arena_width = 20;
  int turn_time = 2;
  int d1 = arena_width;        //move straight (east)
  int d2 = d1 + turn_time;    //turn right 90 degree
  int d3 = d2 + grid_space;    //move forward by robot diameter
  int d4 = d3 + turn_time;    //turn right 90 degree
  int d5 = d4 + arena_width;   //move straight (west)
  int d6 = d5 + turn_time;    //turn left 90 degree
  int d7 = d6 + grid_space;    //move forward by robot diameter
  int d8 = d7 + turn_time;    //turn left 90 degree
  int speed = 30;
  if(count >0 && count < d1){
      move_robot_forward(speed);
  }

  
  if(count > d1 && count < d2){
      turn_robot_right(90);
  }
  
  
  if(count > d2 && count < d3){
      move_robot_forward(speed);
  }
  
  
  if(count > d3 && count < d4){
      turn_robot_right(90);
  }
  
  
  if(count > d4 && count < d5){
      move_robot_forward(speed);
  }
  
  
  if(count > d5 && count < d6){
      turn_robot_left(90);
  }
  
  
  
  if(count > d6 && count < d7){
      move_robot_forward(speed);
  }
  
  
  
  if(count > d7 && count < d8){
      turn_robot_left(90);
  }
 
  
  if(count > d8){
       count = 0; 
  }

}

public void calculate_distance()
{
  int d = 0;
  float hyp = 0;
  for(int i=0;i<n_cities; i++){
    int x = temp_route_x[i] - temp_route_x[i+1];    
    int y = temp_route_y[i] - temp_route_y[i+1];
    hyp = sqrt(x*x + y*y);    
    d = PApplet.parseInt(d + PApplet.parseInt(hyp));
  }

  if(distance_initialized == false){
    shortest_distance = d;

    for(int i=0; i<n_cities; i++){
      shortest_route_x[i] = temp_route_x[i];  
      shortest_route_y[i]  = temp_route_y[i];
    }
    //Copy the last city as first city
    shortest_route_x[n_cities] = temp_route_x[0];  
    shortest_route_y[n_cities] = temp_route_y[0];
    distance_initialized = true;
  }else{
    if(d < shortest_distance){
      shortest_distance = d;
      for(int i=0; i<n_cities; i++){
        shortest_route_x[i] = temp_route_x[i];  
        shortest_route_y[i]  = temp_route_y[i];
      }
      //Copy the last city as first city
      shortest_route_x[n_cities] = temp_route_x[0];  
      shortest_route_y[n_cities] = temp_route_y[0];
    }
  }
}

public void calculate_shortest_path()
{
  combinations();
}

public void combinations()
{
  for(int i=0; i<n_cities; i++){//0 to n-1
    for(int j=0; j<n_cities-1; j++){ //0 to n-2
      int tx = temp_route_x[j];
      temp_route_x[j] = temp_route_x[j+1];
      temp_route_x[j+1] = tx;

      int ty = temp_route_y[j];
      temp_route_y[j] = temp_route_y[j+1];
      temp_route_y[j+1] = ty;
      temp_route_x[n_cities] = x_city[0]; //Always make the last city as the first city
         temp_route_y[n_cities] = y_city[0]; //Always make the last city as the first city
      calculate_distance();
    }
  } 
}

public void move_zig_zag_down_up()
{
  int grid_space = 2;
  int arena_width = 20;
  int turn_time = 2;
  int d1 = arena_width;        //move straight (east)
  int d2 = d1 + turn_time;    //turn right 90 degree
  int d3 = d2 + grid_space;    //move forward by robot diameter
  int d4 = d3 + turn_time;    //turn right 90 degree
  int d5 = d4 + arena_width;   //move straight (west)
  int d6 = d5 + turn_time;    //turn left 90 degree
  int d7 = d6 + grid_space;    //move forward by robot diameter
  int d8 = d7 + turn_time;    //turn left 90 degree
  int speed = 30;
  if(count >0 && count < d1){
      move_robot_forward(speed);
  }

  
  if(count > d1 && count < d2){
      turn_robot_left(90);
  }
  
  
  if(count > d2 && count < d3){
      move_robot_forward(speed);
  }
  
  
  if(count > d3 && count < d4){
      turn_robot_left(90);
  }
  
  
  if(count > d4 && count < d5){
      move_robot_forward(speed);
  }
  
  
  if(count > d5 && count < d6){
      turn_robot_right(90);
  }
  
  
  
  if(count > d6 && count < d7){
      move_robot_forward(speed);
  }
  
  
  
  if(count > d7 && count < d8){
      turn_robot_right(90);
  }
 
  
  if(count > d8){
       count = 0; 
  }

}

public void setup()
{
   x_city = new int[n_cities];
   y_city = new int[n_cities];
   shortest_route_x = new int[n_cities + 1]; //Always the first and last city are same
   shortest_route_y = new int[n_cities + 1];
   temp_route_x = new int[n_cities + 1];
   temp_route_y = new int[n_cities + 1];


    
   background(255);
   draw_predefined_junctions();
   robot_curr_angle = 0;
   robot_curr_x = robot_init_x;
   robot_curr_y = robot_init_y;
   robot();
   calculate_shortest_path();
   print_shortest_route();
   //draw_random_junctions();
   frameRate(10);
}

public void print_shortest_route()
{
   for(int i =0;i<n_cities;i++){
       for(int j =0;j<n_cities;j++){
      if(shortest_route_x[i] == x_city[j] && shortest_route_y[i] == y_city[j]){
        print("->",j,"->");
      }
       }
  }
}

public void plot_shortest_route()
{
   for(int i =0;i<n_cities;i++){
      int x1 = shortest_route_x[i];
      int x2 = shortest_route_x[i+1];
      int y1 = shortest_route_y[i];
      int y2 = shortest_route_y[i+1];
      stroke(0);
      strokeWeight(5);
      line(x1,y1, x2,y2); 
  }
}

int bottom = 0;
public void draw()
{
  refresh_frame();
  plot_shortest_route();
  count += 1;
  if(bottom == 0){
    move_zig_zag_up_down();
  }else if(bottom == 1){
    move_zig_zag_down_up();  
  }
 
  //Check if robot reached the end-left corner or top-left corner of the arena or t
  if(robot_curr_x == (circle_diameter/2) && robot_curr_y == (canvas_width - (circle_diameter/2))){
      bottom = 2; //Setting some random value so that move_zig_zag* will not get executed
      delay(2000);
      turn_robot_right(180);
      bottom = 1;
      count = 0;

  }else if(robot_curr_x == (circle_diameter/2) && robot_curr_y == (circle_diameter/2)){
      bottom = 4;  //Setting some random value so that move_zig_zag* will not get executed
      delay(2000);
      turn_robot_right(180);
      bottom = 0;  
      count = 0;
  }
  
  
  if(robot_curr_x > canvas_width - (circle_diameter/2)){
    robot_curr_x = canvas_width -  (circle_diameter/2);
  }else if (robot_curr_x < (circle_diameter/2)){
     robot_curr_x =  circle_diameter/2;
  }

  if(robot_curr_y > canvas_height - (circle_diameter/2)){
    robot_curr_y = canvas_height -  (circle_diameter/2);
  }else if (robot_curr_y < (circle_diameter/2)){
     robot_curr_y =  circle_diameter/2;
  }  
  
}
  public void settings() {  size(600, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "shortest_path" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
