import { useEffect } from 'react';

const useScrollToBottom = (deps: any[]) => {
    useEffect(() => {
        if (deps.length > 0) {
            window.scrollTo({
                top: document.body.scrollHeight,
                behavior: 'smooth',
            });
        }
    }, [deps]);
};

export default useScrollToBottom;
