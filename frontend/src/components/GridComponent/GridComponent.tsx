import {Grid as GridComp, GridCol} from '@mantine/core';
import styles from './GridComponent.module.css';

interface GridComponentProps {
    gridItems: string[];
    gridItemsMultiplier: number[];
}

/**
 *
 * @param gridItems - titles of columns
 * @param gridItemsMultiplier - multipliers for columns, if we want have wider columns,
 * the multiplier should be bigger, but sum of span has to == 12
 * @constructor
 */
export function GridComponent({gridItems, gridItemsMultiplier}: GridComponentProps) {
    const span = 12;

    return (
        <div style={{height: '100%', width: '80%', margin: '0 auto'}}>
            <GridComp
                className={styles.gridCol}
                breakpoints={{xs: '100px', sm: '200px', md: '300px', lg: '400px', xl: '500px'}}
            >
                {gridItems.map((item, index) => (
                    <GridCol key={index} span={span * gridItemsMultiplier[index]}>
                        {item}
                    </GridCol>
                ))}
            </GridComp>
        </div>
    );
}