int x_city[];
int y_city[];
int n_cities = 6;
int canvas_width = 600;
int canvas_height = 600;
int circle_diameter = 30;
int robot_diameter = 30;
int grid_size = circle_diameter;
float grid_thickness = 0.2;
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

float degree_to_rad(float angle)
{
   float radians = (angle * 3.1415)/180;
   return  radians;
}

float rad_to_degree(float rad)
{
   float angle = rad * (180/3.1415);
   return angle;
}

void draw_random_junctions()
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
      draw_circle(x,y,circle_diameter, 1);
   }
}

void draw_predefined_junctions()
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
   } 
}

void identify_circles_location()
{
  float row = 0.0, column = 0.0;
  int r1 = 0, r2 = 0, c1 =0, c2=0;
  boolean circle_in_r2 = false;
  boolean circle_in_c2 = false;
  int cell_midpoint = circle_diameter / 2;
  for(int i = 0; i < n_cities; i++){
    column = (x_city[i] - 1)/cell_midpoint;
    row = (y_city[i] - 1)/cell_midpoint;
    r1 = int (row / 2);

    if(row%2 > 0.0){
      r2 = r1 + 1;
      circle_in_r2 = true; 
    }else{
      circle_in_r2 = false; 
    }
    
    c1 = int(column/2);

    if(column%2 > 0.0){
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

void inc_angle(float degree)
{
  robot_curr_angle = (robot_curr_angle + degree) % 360.0;
}

void dec_angle(float degree)
{
  robot_curr_angle = (robot_curr_angle - degree) % 360.0;
}

void draw_circle(float x, float y, int diameter, float thickness)
{
     strokeWeight(thickness);
     ellipse(x,y,diameter,diameter);
}
void plot_nodes(int x, int y)
{
    fill(0,0,255);
    //stroke(0);
    draw_circle(x, y, circle_diameter, 0.5);
}

void robot()
{
    float radius = robot_diameter/2;
    fill(0,128,0);
	  draw_circle(robot_curr_x, robot_curr_y, robot_diameter, 0.5);
    stroke(255,255,0);
    strokeWeight(3);
    float x1 = robot_curr_x;
    float y1 = robot_curr_y;
    float x2 = x1 + radius * cos(degree_to_rad(robot_curr_angle));
    float y2 = y1 + radius * sin(degree_to_rad(robot_curr_angle));
    line(x1, y1, x2, y2);
}

void move_robot_forward(float distance)
{
  float new_x = distance * cos(degree_to_rad(robot_curr_angle));
  float new_y = distance * sin(degree_to_rad(robot_curr_angle));
  robot_curr_x +=  new_x;
  robot_curr_y +=  new_y;
  robot();
}

void move_robot_backward(int distance)
{
  float new_x = distance * cos(degree_to_rad(robot_curr_angle));
  float new_y = distance * sin(degree_to_rad(robot_curr_angle));
  robot_curr_x -=  new_x;
  robot_curr_y -=  new_y;
  robot();
}

void turn_robot_right(float angle)
{
  inc_angle(angle);
  robot();
}

void turn_robot_left(float angle)
{
  dec_angle(angle);
  robot();
}

void create_grid_layout(int grid_size, int canvas_width, int canvas_height, float grid_thickness){
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

void refresh_frame()
{
  background(255);
  draw_predefined_junctions();
  create_grid_layout(grid_size, canvas_width, canvas_height, grid_thickness);
}

int get_robot_direction()
{
  int direction = 0;
  float angle = robot_curr_angle;
  if(angle == 0.0 || angle == 360.0){
      direction = east;  
  }else if(angle == 90.0){
      direction = south;  
  }else if(angle == 180.0){
      direction = west;  
  }else if(angle == 270.0){
      direction = north;  
  }else if(angle > 0.0 && angle < 90.0){
     direction = south_east; 
  }else if(angle > 90.0 && angle < 180.0){
     direction = south_west;
  }else if(angle > 180.0 && angle < 270.0){
    direction = north_west;
  }else if(angle > 270.0 && angle < 360.0){
    direction = north_east; 
  }
  
  return direction;
}

void move_zig_zag_up_down()
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

void move_zig_zag_down_up()
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

void setup()
{
   x_city = new int[n_cities];
   y_city = new int[n_cities];
   size(600, 600); 
   background(255);
   draw_predefined_junctions();
   robot_curr_angle = 0;
   robot_curr_x = robot_init_x;
   robot_curr_y = robot_init_y;
   robot();
   //draw_random_junctions();
   frameRate(10);
}

int bottom = 0;
void draw()
{
  refresh_frame();
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
