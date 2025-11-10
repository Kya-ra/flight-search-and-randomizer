import type { Todo } from "./TodoForm";

interface Props {
  todos: Todo[];
  onRemove: (id: number) => void;
}

export default function TodoList({ todos, onRemove }: Props) {
  return (
    <ul>
      {todos.map(todo => (
        <li key={todo.id}>
          {todo.text}{" "}
          <button onClick={() => onRemove(todo.id)}>❌</button>
        </li>
      ))}
    </ul>
  );
}
