package com.habitos.domain.model

class HabitAlreadyCompletedException : Exception("Hábito já foi concluído para a data especificada.")
class CannotCompleteInPastException : Exception("Só é possível concluir hábitos na data de hoje.")
class CompletionNotFoundException : Exception("A conclusão não foi encontrada para a data especificada.")
