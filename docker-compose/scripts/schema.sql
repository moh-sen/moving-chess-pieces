CREATE TABLE chess_pieces (
	id VARCHAR(128),
	piece_type ENUM('ROOK', 'BISHOP'),
	piece_row TINYINT(1) UNSIGNED,
	piece_col TINYINT(1) UNSIGNED,
	is_removed BOOLEAN
);
