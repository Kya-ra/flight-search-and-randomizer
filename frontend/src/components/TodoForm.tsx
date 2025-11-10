import { useState } from "react";

export type Todo  = {
  id: number;
  text: string;
}

interface Props {
  onAdd: (todo: Todo) => void;
}

export default function TodoForm({ onAdd }: Props) {
  const [text, setText] = useState("");

  function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (!text.trim()) return;

    onAdd({ id: Date.now(), text });
    setText("");
  }

  return (
    <form onSubmit={handleSubmit}>
      <input
        value={text}
        onChange={e => setText(e.target.value)}
        placeholder="New task..."
      />
      <button type="submit">Add</button>
    </form>
  );
}
