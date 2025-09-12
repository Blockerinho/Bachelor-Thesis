%%% Giugno 2025
%%% Aleksa Aleksic
%%% TESTED WITH CLINGO 4.5.3
#const t=24.
tempo(0..t).
val(0..15).
range(0..3).
% 0  1  2  3    % riga 0
% 4  5  6  7    % riga 1
% 8  9 10 11    % riga 2
%12 13 14 15    % riga 3
%%%%%%%
% 0 1 2 3  colonne
%%%%%%
%% Predicato 
%cell(tempo T, riga X, colonna Y, valore V)
%%% Input (cambiarli)
cell(0,0,0,0).  cell(0,0,1,1).  cell(0,0,2,7).  cell(0,0,3,3).
cell(0,1,0,2).  cell(0,1,1,6).  cell(0,1,2,8).  cell(0,1,3,4).
cell(0,2,0,5).  cell(0,2,1,9). cell(0,2,2,11). cell(0,2,3,12).
cell(0,3,0,13). cell(0,3,1,10). cell(0,3,2,14). cell(0,3,3,15).
%%% Goal
goal(X,Y) :- range(X), range(Y), X < 3, cell(t,X,Y,4*X+Y+1).
goal(X,Y) :- range(X), range(Y), X = 3, Y < 3, cell(t,X,Y,4*X+Y+1).
goal(3,3) :- cell(t,3,3,0).
:- range(X),range(Y), not goal(X,Y).

%%% Una mossa (sposta il buco!)
1{ move(T,up); move(T,down); move(T,left); move(T,right)}1 :- tempo(T), tempo(T+1).

hole(T,X,Y) :- cell(T,X,Y,0), tempo(T), range(X), range(Y).

% Mosse proibite
:- tempo(T), move(T,up),    range(Y), hole(T,0,Y). 
:- tempo(T), move(T,down),  range(Y), hole(T,3,Y). 
:- tempo(T), move(T,left),  range(X), hole(T,X,0). 
:- tempo(T), move(T,right), range(X), hole(T,X,3). 

% Effetti
moved(T,X-1,Y) :-  hole(T,X,Y), move(T,up),    tempo(T),  range(X), range(Y).
moved(T,X+1,Y) :-  hole(T,X,Y), move(T,down),  tempo(T),  range(X), range(Y).
moved(T,X,Y-1) :-  hole(T,X,Y), move(T,left),  tempo(T),  range(X), range(Y).
moved(T,X,Y+1) :-  hole(T,X,Y), move(T,right), tempo(T),  range(X), range(Y).

cell(T+1,X,Y,0)     :-  
     moved(T,X,Y), tempo(T), tempo(T+1), range(X), range(Y). 
cell(T+1,X1,Y1,V)   :-  
    hole(T,X1,Y1), moved(T,X2,Y2), cell(T,X2,Y2,V), 
    tempo(T), tempo(T+1), range(X1), range(X2), range(Y1), range(Y2), val(V).

% Inerzia
affected(T,X,Y) :- hole(T,X,Y),  tempo(T),  range(X), range(Y).
affected(T,X,Y) :- moved(T,X,Y), tempo(T),  range(X), range(Y).

%% sono le celle che non sono state spostate in sostanza perché valga cell(T+1,X,Y,V) devono valere le tre cose dopo.
cell(T+1,X,Y,V) :-  cell(T,X,Y,V),
       not affected(T,X,Y), 
       tempo(T), tempo(T+1), range(X), range(Y), val(V).

#show move/2.
#minimize{1,T : move(T,_)}.