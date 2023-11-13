import pygame, sys, random
import websocket
from pygame.event import Event
import threading
import json


'''What's imp
Movement
Control
Collision Detection
Scoring
AI'''

'''Divide and conquer'''

'''vARIABLES, OBJECTS

uPDATING POSITIONS'
Checking for collision'''


'''There is a colliderect method which return true if it collides with one triangle'''
def point_won(winner):
    global cpu_points, player_points

    if winner == "cpu":
        #cpu_points += 1
        print('1')
    if winner == "player":
        #player_points += 1
        #pygame.quit
        print('2')

player1_up = pygame.event.custom_type() #No use ok it is for you to use this
player1_down = pygame.event.custom_type()
player2_up = pygame.event.custom_type()
player2_down = pygame.event.custom_type()
release1 = pygame.event.custom_type()
release2 = pygame.event.custom_type()
keyup = pygame.event.custom_type()
keydown = pygame.event.custom_type()

def on_message(wsapp, message):
    data = json.loads(message)
    player = data['player']
    isPressed = data['pressed']
    if isPressed:
        move = data['direction']

        if move and player:
            pygame.event.post(Event(player1_up))
        elif not move and player:
            pygame.event.post(Event(player1_down))
        elif move and not player:
            pygame.event.post(Event(player2_up))
        elif not move and not player:
            pygame.event.post(Event(player2_down))
    else:
        if(player):
            pygame.event.post(Event(release1))
        else:
            pygame.event.post(Event(release2))


# websocket.enableTrace(True)
wsapp = websocket.WebSocketApp("ws://192.168.75.239:3000/game", on_message=on_message)
wsapp_thread = threading.Thread(target=wsapp.run_forever)
wsapp_thread.daemon = True
wsapp_thread.start()    


def animate_bally():
    global bally_speed_x, bally_speed_y
    global cpu_points, player_points
    bally.x+= bally_speed_x   #this will increase each by pixel
    bally.y+= bally_speed_y

    if bally.bottom >= screen_height or bally.top<= 0:
       bally_speed_y *= -1

    '''if bally.right >= screen_width: #ye hum bana rahe the score diplay karne ke liye
        cpu_points+=1
        point_won("cpu")
        pygame.quit()
        
        
    if bally.left <= 0:
        player_points+=1
        point_won("player")
        pygame.quit()'''

    if bally.left <= 0: 
        bally_start()
        player_points += 1

	# Opponent Score
    if bally.right >= screen_width:
        bally_start()
        cpu_points += 1    
        

    if bally.colliderect(player) or bally.colliderect(cpu):
        bally_speed_x *= -1

def animate_player():
    player.y += player_speed
    if player.top <= 0:
        player.top=0
    if player.bottom >= screen_height:
        player.bottom = screen_height

def  animate_cpu():
    
    cpu.y += cpu_speed
    

    if cpu.top <= 0:
        cpu.top = 0
    if cpu.bottom>= screen_height:
        cpu.bottom = screen_height


def bally_start():
    global bally_speed_x, bally_speed_y

    bally.center = (screen_width/2, screen_height/2)
    bally_speed_y *= random.choice((1,-1))
    bally_speed_x *= random.choice((1,-1))

pygame.init()



screen_width = 1280
screen_height = 600

screen = pygame.display.set_mode((screen_width, screen_height))
#Giving title
pygame.display.set_caption("My Pong Game")

#Getting clock object to control the frame rate of the game, how fast the game will run.
clock = pygame.time.Clock()

'''Definitions part completed'''
BLACK = (255,229,180)
RED = (210, 4, 45)
#create screen
#screen = pygame.display.set_mode([1280,720])



# Set the dimensions of the object
object_width = 30
object_height = 30

# Set the initial position of the object
object_x = 0  
object_y = 0

bally= pygame.draw.rect(screen, RED, (object_x, object_y, object_width, object_height))
bally.center= (screen_width/2, screen_height/2)


cpu = pygame.Rect(0,0,20,100)
cpu.centery = screen_height/2

player = pygame.Rect(0,0,20,100)
player.midright = (screen_width, screen_height/2)

bally_speed_x = 6
bally_speed_y = 6


player_speed = 0
cpu_speed = 0

player_points=0
cpu_points=0

bally_speed_x = 6 * random.choice((1,-1))
bally_speed_y = 6 * random.choice((1,-1))



basic_font = pygame.font.Font('freesansbold.ttf', 32)




'''Now GAME LOOP part''' 
#Check for events
#Update the positions of the game objects
#Draw the game objects and update the screen

while True:
    for event in pygame.event.get():#This line gets all the events that pygame recognizes and happened since the last time the while loop ran and puts them in a list
        if event.type == pygame.QUIT:
            pygame.quit()
            sys.exit()
        
        if event.type == player1_up:
            player_speed = -6
        if event.type == player1_down:
            player_speed = +6
        if event.type == player2_up:
            cpu_speed = -6
        if event.type == player2_down:
            cpu_speed = +6
        if event.type == release1:
            player_speed = 0
        if event.type == release2:
            cpu_speed = 0
    
        # if event.key == cpu_up:
        #     cpu_speed = -6
        # if event.key == cpu_down:
        #     cpu_speed = +6
    
        # if event.key == pygame.K_UP:
        #     cpu_speed = 0
        # if event.key == pygame.K_DOWN:
        #     cpu_speed = 0      
        # keys = pygame.key.get_pressed()
        # if keys[pygame.K_w]:        #here!! kk
        #     cpu_speed= -6
        # elif keys[pygame.K_s]:
        #     cpu_speed = +6
        # else:
        #     cpu_speed = 0    
            
        # if keys[pygame.K_UP]:
        #     player_speed = -6
        # elif keys[pygame.K_DOWN]:
        #     player_speed = +6
        # else:
        #     player_speed=0     
        
            
    animate_bally()
    animate_player()
    animate_cpu()


    '''bally.x+= bally_speed_x   #this will increase each by pixel
    bally.y+= bally_speed_y
    if bally.bottom >= screen_height or bally.top<= 0:
        bally_speed_y *= -1
    if bally.right >= screen_width or bally.left <= 0:
        bally_speed_x *= -1'''
        
    #Draw game objects
    screen.fill(BLACK) #does not leave mark when object moves
    #crate rectangle
    #surface = pygame.Surface([100,100])
    #surface.fill(RED)
    #rectangle=surface.get_rect()
    #give position of our rectangle
    #rectangle.x=640
    #rectangle.y = 300
    
    #draw rectangle
    pygame.draw.aaline(screen, 'black', (screen_width/2,0), (screen_width/2, screen_height))
    pygame.draw.ellipse(screen, RED, bally)
    pygame.draw.rect(screen, 'black', cpu)
    pygame.draw.rect(screen, 'black', player)

    player_text = basic_font.render(f'{player_points}',False,'grey')
    screen.blit(player_text,(660,470))

    cpu_text = basic_font.render(f'{cpu_points}',False,'grey')
    screen.blit(cpu_text,(600,470))

    pygame.display.flip()     
    #Update the diplay
    pygame.display.update

    #Now we need to tell the object how fast it should run
    #that's why we will use tick() function
    #It takes an integer as an argument an that integer is the number of frames per second that we want
    clock.tick(50)

pygame.display.quit()
pygame.quit()

    #How to draw things on the surface

