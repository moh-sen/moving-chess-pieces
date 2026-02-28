# Recruitment task Beone project

Your task is to create an application, that will enable the user to move chess pieces on a chess table, using REST API.

![chess.png](doc%2Fchess.png)

### Domain Requirements
1. Chess table is always 8x8 and each field can be described as a tuple e.g. (3,0), both values can be int, there is no need to keep the original chess position naming like "A4". 
1. On a single field there can be always only a single chess piece (pieces are of the same color so they can't interact with each other).
1. There are only two possible chess pieces: rook (The rook moves horizontally or vertically, through any number of unoccupied squares) and bishop (The bishop moves diagonally in any direction it wishes and as far as it wishes as long as the squares are free).
1. Each piece can be removed from the table, but we need to keep its last position, a piece that was removed can't be placed on the table again
1. There should be a possibility to put a new piece (of a given type) on an empty chess field and assign a unique ID (you can choose how ID should look like) to it during this action.
1. There should be a possibility to move a piece to a selected field given an id of the chess piece that is on the table.

