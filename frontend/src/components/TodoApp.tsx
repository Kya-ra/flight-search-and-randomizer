import { useState } from "react";
import TodoForm from "./TodoForm";
import type { Todo } from "./TodoForm"
import TodoList from "./TodoList";

export default function TodoApp() {
  const [todos, setTodos] = useState<Todo[]>([]);

  function handleAdd(todo: Todo) {
    setTodos(prev => [...prev, todo]);
  }

  function handleRemove(id: number) {
    setTodos(prev => prev.filter(todo => todo.id !== id));
  }

  return (
    <div>
      <h1>Todo List</h1>
      <TodoForm onAdd={handleAdd} />
      <TodoList todos={todos} onRemove={handleRemove} />
    </div>
  );
}
